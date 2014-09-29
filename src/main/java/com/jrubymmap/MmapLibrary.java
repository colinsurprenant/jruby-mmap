package com.jrubymmap;

import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubyFixnum;
import org.jruby.RubyInteger;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.Library;

public class MmapLibrary implements Library {
    public void load(Ruby runtime, boolean wrap) throws IOException {
        RubyClass mmapClass = runtime.defineClass("Mmap", runtime.getObject(),  new ObjectAllocator() {
            public IRubyObject allocate(Ruby runtime, RubyClass rubyClass) {
                return new Mmap(runtime, rubyClass);
            }
        });
        mmapClass.defineAnnotatedMethods(Mmap.class);
    }

    @JRubyClass(name = "Mmap", parent = "Object")
    public static class Mmap extends RubyObject {

        private long size;
        private FileChannel channel;
        private RandomAccessFile raf;
        private MappedByteBuffer buffer;

        public Mmap(Ruby runtime, RubyClass klass) {
            super(runtime, klass);
        }

        @JRubyMethod(name = "initialize", required = 2)
        public IRubyObject initialize(ThreadContext context, RubyString path, IRubyObject size)
            throws IOException
        {
            this.size = size.convertToInteger().getLongValue();
            File file = new File(path.decodeString());
            this.raf = new RandomAccessFile(file, "rw");
            this.channel = this.raf.getChannel();
            this.buffer = this.channel.map(FileChannel.MapMode.READ_WRITE, 0, this.size);
            this.raf.close();
            return context.nil;
        }

        @JRubyMethod(name = "seek", required = 1)
        public void seek(IRubyObject pos)
            throws IOException
        {
            this.buffer = this.channel.map(FileChannel.MapMode.READ_WRITE, pos.convertToInteger().getLongValue(), this.size);
        }

        @JRubyMethod(name = "write", required = 1)
        public void write(RubyString data) {
            this.buffer.put(data.getByteList().unsafeBytes());
        }

        @JRubyMethod(name = "safe_write", required = 1)
        public void safe_write(RubyString data) {
            this.buffer.put(data.getByteList().bytes());
        }

        @JRubyMethod(name = "position")
        public IRubyObject position(ThreadContext context)
            throws IOException
        {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.buffer.position());
        }

        @JRubyMethod(name = "limit")
        public IRubyObject limit(ThreadContext context)
            throws IOException
        {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.buffer.limit());
        }

        @JRubyMethod(name = "close")
        public void close()
            throws IOException
        {
            this.channel.close();
        }

    }
}