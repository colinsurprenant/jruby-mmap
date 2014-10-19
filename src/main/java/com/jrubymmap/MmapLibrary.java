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
import org.jruby.util.ByteList;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.Library;

public class MmapLibrary implements Library {
    public void load(Ruby runtime, boolean wrap) throws IOException {
        RubyModule mmapModule = runtime.defineModule("Mmap");
        RubyClass byteBufferClass = runtime.defineClassUnder("ByteBuffer", runtime.getObject(),  new ObjectAllocator() {
            public IRubyObject allocate(Ruby runtime, RubyClass rubyClass) {
                return new ByteBuffer(runtime, rubyClass);
            }
        }, mmapModule);
        byteBufferClass.defineAnnotatedMethods(ByteBuffer.class);
    }

    @JRubyClass(name = "ByteBuffer", parent = "Object")
    public static class ByteBuffer extends RubyObject {

        private long size;
        private FileChannel channel;
        private MappedByteBuffer buffer;

        public ByteBuffer(Ruby runtime, RubyClass klass) {
            super(runtime, klass);
        }

        // def initialize(path, size)
        @JRubyMethod(name = "initialize", required = 2)
        public IRubyObject initialize(ThreadContext context, RubyString path, IRubyObject size)
            throws IOException
        {
            this.size = RubyNumeric.num2long(size);
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
            this.buffer.position(RubyNumeric.num2int(pos));
        }

        // def put_int(value)
        // def put_int(value, index)
        @JRubyMethod(name = "put_int", required = 1, optional = 1)
        public void put_int(IRubyObject[] args) {
            int value = RubyNumeric.num2int(args[0]);
            if (args.length > 1) {
                int index = RubyNumeric.num2int(args[1]);
                this.buffer.putInt(index, value);
            } else {
                this.buffer.putInt(value);
            }
        }

        // def get_int
        // def get_int(index)
        @JRubyMethod(name = "get_int", required = 0, optional = 1)
        public IRubyObject get_int(ThreadContext context, IRubyObject[] args) {
            Ruby runtime = context.runtime;
            if (args.length > 0) {
                int index = RubyNumeric.num2int(args[0]);
                return runtime.newFixnum(this.buffer.getInt(index));
            } else {
                return runtime.newFixnum(this.buffer.getInt());
            }
        }

        // def put_long(value)
        // def put_long(value, index)
        @JRubyMethod(name = "put_long", required = 1, optional = 1)
        public void put_long(IRubyObject[] args) {
            long value = RubyNumeric.num2long(args[0]);
            if (args.length > 1) {
                int index = RubyNumeric.num2int(args[1]);
                this.buffer.putLong(index, value);
            } else {
                this.buffer.putLong(value);
            }
        }

        // def get_long
        // def get_long(index)
        @JRubyMethod(name = "get_long", required = 0, optional = 1)
        public IRubyObject get_long(ThreadContext context, IRubyObject[] args) {
            Ruby runtime = context.runtime;
            if (args.length > 0) {
                int index = RubyNumeric.num2int(args[0]);
                return runtime.newFixnum(this.buffer.getLong(index));
            } else {
                return runtime.newFixnum(this.buffer.getLong());
            }
        }

        // relative put the direct underlying bytes without copy
        //
        // def put_bytes(string)
        // def put_bytes(string, offset, length)
        @JRubyMethod(name = "put_bytes", required = 1, optional = 2)
        public void put_bytes(ThreadContext context, IRubyObject[] args) {
            if (args.length == 3) {
                int offset = RubyNumeric.num2int(args[1]);
                int length = RubyNumeric.num2int(args[2]);
                this.buffer.put(((RubyString)args[0]).getByteList().unsafeBytes(), offset, length);
            } else if (args.length == 1) {
                ByteList byteList = ((RubyString)args[0]).getByteList();
                // byteList may have a backing byte[] longer than the actual string bytes
                // make sure to use only 0..byteList.length()
                this.buffer.put(byteList.unsafeBytes(), 0, byteList.length());
            } else {
                throw context.runtime.newArgumentError("Invalid number of parameters");
            }
        }

        // relative put a copy of the underlying bytes
        //
        // def put_bytes_copy(string)
        // def put_bytes_copy(string, offset, length)
        @JRubyMethod(name = "put_bytes_copy", required = 1, optional = 2)
        public void put_bytes_copy(ThreadContext context, IRubyObject[] args) {
            if (args.length == 3) {
                int offset = RubyNumeric.num2int(args[1]);
                int length = RubyNumeric.num2int(args[2]);
                this.buffer.put(((RubyString)args[0]).getBytes(), offset, length);
            } else if (args.length == 1) {
                // getBytes() copies the bytes in a new byte[] size of the exact string bytesize
                this.buffer.put(((RubyString)args[0]).getBytes());
            } else {
                throw context.runtime.newArgumentError("Invalid number of parameters");
            }
        }

        // def get_bytes(size)
        // def get_bytes(size, offset, length)
        @JRubyMethod(name = "get_bytes", required = 1, optional = 2)
        public IRubyObject get_bytes(ThreadContext context, IRubyObject[] args) {
            // byte[] bytes = new byte[(int)((RubyFixnum)args[0]).getLongValue()];
            byte[] bytes = new byte[RubyNumeric.num2int(args[0])];

            if (args.length == 3) {
                int offset = RubyNumeric.num2int(args[1]);
                int length = RubyNumeric.num2int(args[2]);
                this.buffer.get(bytes, offset, length);
            } else if (args.length == 1) {
                this.buffer.get(bytes);
            } else {
                throw context.runtime.newArgumentError("Invalid number of parameters");
            }

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