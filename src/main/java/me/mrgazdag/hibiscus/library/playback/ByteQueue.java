package me.mrgazdag.hibiscus.library.playback;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class ByteQueue {
    private final byte[] theBuffer;

    private int readCursor;
    private int writeCursor;
    private boolean hasContents;

    public ByteQueue(int size) {
        this.theBuffer = new byte[size];
        this.readCursor = 0;
        this.writeCursor = 0;
        this.hasContents = false;
    }
    public void read(byte[] buffer, int offset, int length) {
        synchronized (theBuffer) {
            int read;
            while (length > 0) {
                read = readNoBlocking(buffer, offset, length);
                length -= read;
                if (length > 0) try {theBuffer.wait();} catch (InterruptedException ignored) {}
            }
        }
    }

    private int readNoBlocking(byte[] buffer, int offset, int length) {
        if (writeCursor > readCursor) {
            length = Math.min(length, writeCursor-readCursor);
            if (length == (writeCursor - readCursor)) hasContents = false;
            System.arraycopy(theBuffer, readCursor, buffer, offset, length);
            readCursor += length;

            theBuffer.notifyAll();
            return length;
        } else if (readCursor == writeCursor && !hasContents) {
            //buffer already read, no new contents
            return 0;
        } else if (theBuffer.length - readCursor > length) {
            //we can read it in one go
            System.arraycopy(theBuffer, readCursor, buffer, offset, length);
            readCursor += length;

            theBuffer.notifyAll();
            return length;
        } else {
            //we will need to wrap around
            int first = theBuffer.length - readCursor;
            System.arraycopy(theBuffer, readCursor, buffer, offset, first);

            //read the second part
            length = Math.min(length-first, writeCursor);
            if (length == writeCursor) hasContents = false;
            if (length > 0) System.arraycopy(theBuffer, 0 /* start of the array */, buffer, offset+first, length-first);
            readCursor = length;

            theBuffer.notifyAll();
            return first + length;
        }
    }

    public void read(Iterable<OutputStream> stream, int length) {
        synchronized (theBuffer) {
            int read;
            while (length > 0) {
                read = readNoBlocking(stream, length);
                length -= read;
                if (length > 0) try {theBuffer.wait();} catch (InterruptedException ignored) {}
            }
        }
    }

    private int readNoBlocking(Iterable<OutputStream> stream, int length) {
        Iterator<OutputStream> it = stream.iterator();
        if (writeCursor > readCursor) {
            length = Math.min(length, writeCursor-readCursor);
            if (length == (writeCursor - readCursor)) hasContents = false;
            writeToStream(it, readCursor, length);
            readCursor += length;

            theBuffer.notifyAll();
            return length;
        } else if (readCursor == writeCursor && !hasContents) {
            //buffer already read, no new contents
            return 0;
        } else if (theBuffer.length - readCursor > length) {
            //we can read it in one go
            writeToStream(it, readCursor, length);
            readCursor += length;

            theBuffer.notifyAll();
            return length;
        } else {
            //we will need to wrap around
            int first = theBuffer.length - readCursor;
            writeToStream(it, readCursor, first);

            //read the second part
            it = stream.iterator();
            length = Math.min(length-first, writeCursor);
            if (length == writeCursor) hasContents = false;
            if (length > 0) writeToStream(it, 0 /* start of the array */, length-first);
            readCursor = length;

            theBuffer.notifyAll();
            return first + length;
        }
    }
    private void writeToStream(Iterator<OutputStream> it, int offset, int length) {
        while (it.hasNext()) {
            OutputStream out = it.next();
            try {
                out.write(theBuffer, offset, length);
            } catch (IOException e) {
                it.remove();
                try {
                    out.flush();
                    out.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * This method tries to write the specified buffer's contents
     * into the buffer. If this cannot be done, then this method will
     * block until the specified amount of data can be written.
     * @param buffer the buffer to write
     * @param offset the offset at which to start
     * @param length the amount of bytes to write
     */
    public synchronized void write(byte[] buffer, int offset, int length) {
        synchronized (theBuffer) {
            int written;
            while (length > 0) {
                written = writeNoBlocking(buffer, offset, length);
                length -= written;
                offset += written;
                if (length > 0) try {theBuffer.wait();} catch (InterruptedException ignored) {}
            }
        }
    }


    /**
     * This method will try to write as many bytes as it can without blocking.
     * This method updates the <code>writeCursor</code>.
     * @param buffer the buffer to write
     * @param offset the offset at which to start
     * @param length the amount of bytes to write
     * @return how many bytes have been successfully written
     */
    private int writeNoBlocking(byte[] buffer, int offset, int length) {
        if (readCursor > writeCursor) {
            length = Math.min(length, readCursor-writeCursor);
            System.arraycopy(buffer, offset, theBuffer, writeCursor, length);
            writeCursor += length;
            hasContents = true;
            theBuffer.notifyAll();
            return length;
        } else if (readCursor == writeCursor && hasContents) {
            //buffer already full
            return 0;
        } else if (theBuffer.length - writeCursor > length) {
            //the buffer can fit without overflow
            System.arraycopy(buffer, offset, theBuffer, writeCursor, length);
            writeCursor += length;
            hasContents = true;
            theBuffer.notifyAll();
            return length;
        } else {
            //the buffer will overflow, write the first part first
            int first = theBuffer.length - writeCursor;
            System.arraycopy(buffer, offset, theBuffer, writeCursor, first);

            //write the second part
            length = Math.min(length-first, readCursor);
            if (length > 0) System.arraycopy(buffer, offset+first, theBuffer, 0 /* start of the array */, length);
            writeCursor = length;
            hasContents = true;
            theBuffer.notifyAll();
            return first + length;
        }
    }
}
