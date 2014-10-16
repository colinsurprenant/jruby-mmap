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
        private MappedByteBuffer buffer;

        public Mmap(Ruby runtime, RubyClass klass) {
            super(runtime, klass);
        }

        @JRubyMethod(name = "initialize", required = 2)
        public IRubyObject initialize(ThreadContext context, RubyString path, IRubyObject size)
            throws IOException
        {
            this.size = ((RubyFixnum)size).getLongValue();
            File file = new File(path.decodeString());
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            this.channel = raf.getChannel();
            this.buffer = this.channel.map(FileChannel.MapMode.READ_WRITE, 0, this.size);
            raf.close();
            return context.nil;
        }

        //  best effort to ensure that this buffer content is resident in physical memory
        @JRubyMethod(name = {"load"})
        public void load(ThreadContext context) {
            this.buffer.load();
        }

        @JRubyMethod(name = "size")
        public IRubyObject size(ThreadContext context) {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.size);
        }

        @JRubyMethod(name = {"position="}, required = 1)
        public void set_position(IRubyObject pos)
            throws IOException
        {
            this.buffer.position((int)((RubyFixnum)pos).getLongValue());
        }

        @JRubyMethod(name = "put_int", required = 1)
        public void put_int(IRubyObject value) {
            this.buffer.putInt((int)((RubyFixnum)value).getLongValue());
        }

        @JRubyMethod(name = "put_int_at", required = 2)
        public void put_int_at(IRubyObject index, IRubyObject value) {
            this.buffer.putInt((int)((RubyFixnum)index).getLongValue(), (int)((RubyFixnum)value).getLongValue());
        }

        @JRubyMethod(name = "get_int_at", required = 1)
        public IRubyObject get_int_at(ThreadContext context, IRubyObject index) {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.buffer.getInt((int)((RubyFixnum)index).getLongValue()));
        }

        @JRubyMethod(name = "get_int")
        public IRubyObject get_int(ThreadContext context) {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.buffer.getInt());
        }

        @JRubyMethod(name = "put_long", required = 1)
        public void put_long(IRubyObject value) {
            this.buffer.putLong(((RubyFixnum)value).getLongValue());
        }

        @JRubyMethod(name = "put_long_at", required = 2)
        public void put_long_at(IRubyObject index, IRubyObject value) {
            this.buffer.putLong((int)((RubyFixnum)index).getLongValue(), ((RubyFixnum)value).getLongValue());
        }

        @JRubyMethod(name = "get_long_at", required = 1)
        public IRubyObject get_long_at(ThreadContext context, IRubyObject index) {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.buffer.getLong((int)((RubyFixnum)index).getLongValue()));
        }

        @JRubyMethod(name = "get_long")
        public IRubyObject get_long(ThreadContext context) {
            Ruby runtime = context.runtime;
            return runtime.newFixnum(this.buffer.getLong());
        }

        // put the direct underlying bytes without copy
        @JRubyMethod(name = "put_bytes", required = 1)
        public void put_bytes(RubyString data) {
            this.buffer.put(data.getByteList().unsafeBytes());
        }

        // put a copy of the underlying bytes
        @JRubyMethod(name = "put_bytes_copy", required = 1)
        public void put_bytes_copy(RubyString data) {
            this.buffer.put(data.getByteList().bytes());
        }

        @JRubyMethod(name = "get_bytes", required = 1)
        public IRubyObject get_bytes(ThreadContext context, IRubyObject size) {
            byte[] bytes = new byte[(int)((RubyFixnum)size).getLongValue()];
            this.buffer.get(bytes);
            return RubyString.newString(context.runtime, bytes);
        }

        @JRubyMethod(name = "position")
        public IRubyObject get_position(ThreadContext context)
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