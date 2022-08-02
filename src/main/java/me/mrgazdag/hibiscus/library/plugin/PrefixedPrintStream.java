package me.mrgazdag.hibiscus.library.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class PrefixedPrintStream extends PrintStream {
    private boolean newLine;
    private final String prefix;

    public PrefixedPrintStream(PrintStream delegate, String prefix) {
        super(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                delegate.write(b);
            }
        });
        this.newLine = true;
        this.prefix = prefix;
    }

    @Override
    public void println() {
        super.println();
        newLine = true;
    }

    @Override
    public void print(String s) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(s);
    }

    @Override
    public void print(int i) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(i);
    }

    @Override
    public void print(char c) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(c);
    }

    @Override
    public void print(long l) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(l);
    }

    @Override
    public void print(float f) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(f);
    }

    @Override
    public void print(char[] s) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(s);
    }

    @Override
    public void print(double d) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(d);
    }

    @Override
    public void print(boolean b) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(b);
    }

    @Override
    public void print(Object obj) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.print(obj);
    }

    @Override
    public void println(int x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(char x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(long x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(float x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(char[] x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(double x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(Object x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(String x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }

    @Override
    public void println(boolean x) {
        if (newLine) {super.print(prefix); newLine = false;}
        super.println(x);
        newLine = true;
    }
}
