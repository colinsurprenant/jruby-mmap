require 'spec_helper'
# require "tempfile"

describe Mmap do
  INT_BYTES = 4
  LONG_BYTES = 8

  before(:all) do
    @path = "spec_mmap_file.dat"
    @size = 2048
    File.delete(@path) if File.exist?(@path)
  end

  after(:each) do
    File.delete(@path) if File.exist?(@path)
  end

  it "should create a file with specified size" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    expect(File.size(@path)).to eq(mmap.size)
    mmap.close
  end

  it "should be at position 0 upon initialize" do
    mmap = Mmap::ByteBuffer.new(@path, @size)
    expect(mmap.position).to eq(0)
    mmap.close
  end

  it "should relative put/get integer" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    mmap.put_int(1967)
    expect(mmap.position).to eq(INT_BYTES)
    mmap.position = 0
    expect(mmap.get_int).to eq(1967)
    mmap.close
  end

  it "should absolute put/get integer" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    mmap.put_int(1, 1 * INT_BYTES)
    expect(mmap.position).to eq(0)
    mmap.put_int(Java::java.lang.Integer::MAX_VALUE, 3 * INT_BYTES)
    expect(mmap.position).to eq(0)
    mmap.put_int(2, 2 * INT_BYTES)
    expect(mmap.position).to eq(0)
    mmap.put_int(0, 0 * INT_BYTES)
    expect(mmap.position).to eq(0)

    expect(mmap.get_int(3 * INT_BYTES)).to eq(Java::java.lang.Integer::MAX_VALUE)
    expect(mmap.position).to eq(0)
    expect(mmap.get_int(0 * INT_BYTES)).to eq(0)
    expect(mmap.position).to eq(0)
    expect(mmap.get_int(2 * INT_BYTES)).to eq(2)
    expect(mmap.position).to eq(0)
    expect(mmap.get_int(1 * INT_BYTES)).to eq(1)
    expect(mmap.position).to eq(0)

    expect{mmap.put_int(Java::java.lang.Integer::MAX_VALUE + 1, 0)}.to raise_error(RangeError)

    mmap.close
  end

  it "should relative put/get long" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    mmap.put_long(1967)
    expect(mmap.position).to eq(LONG_BYTES)
    mmap.position = 0
    expect(mmap.get_long).to eq(1967)
    mmap.close
  end

  it "should absolute put/get long" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    mmap.put_long(1, 1 * LONG_BYTES)
    expect(mmap.position).to eq(0)
    mmap.put_long(Java::java.lang.Long::MAX_VALUE, 3 * LONG_BYTES)
    expect(mmap.position).to eq(0)
    mmap.put_long(2, 2 * LONG_BYTES)
    expect(mmap.position).to eq(0)
    mmap.put_long(0, 0 * LONG_BYTES)
    expect(mmap.position).to eq(0)

    expect(mmap.get_long(3 * LONG_BYTES)).to eq(Java::java.lang.Long::MAX_VALUE)
    expect(mmap.position).to eq(0)
    expect(mmap.get_long(0 * LONG_BYTES)).to eq(0)
    expect(mmap.position).to eq(0)
    expect(mmap.get_long(2 * LONG_BYTES)).to eq(2)
    expect(mmap.position).to eq(0)
    expect(mmap.get_long(1 * LONG_BYTES)).to eq(1)
    expect(mmap.position).to eq(0)

    expect{mmap.put_long(Java::java.lang.Long::MAX_VALUE + 1, 0)}.to raise_error(RangeError)

    mmap.close
  end

  it "should relative put/get bytes" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    s = "hello world"
    expect(s.size).to eq(11)
    expect(s.bytesize).to eq(11)
    mmap.put_bytes(s)
    expect(mmap.position).to eq(11)

    s = "foobar"
    expect(s.size).to eq(6)
    expect(s.bytesize).to eq(6)
    mmap.put_bytes(s)
    expect(mmap.position).to eq(17)

    mmap.position = 0
    expect(mmap.get_bytes(11)).to eq("hello world")
    expect(mmap.get_bytes(6)).to eq("foobar")
    mmap.close
  end

  it "should relative put/get bytes copy" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    s = "hello world"
    expect(s.size).to eq(11)
    expect(s.bytesize).to eq(11)
    mmap.put_bytes_copy(s)
    expect(mmap.position).to eq(11)

    s = "foobar"
    expect(s.size).to eq(6)
    expect(s.bytesize).to eq(6)
    mmap.put_bytes_copy(s)
    expect(mmap.position).to eq(17)

    mmap.position = 0
    expect(mmap.get_bytes(11)).to eq("hello world")
    expect(mmap.get_bytes(6)).to eq("foobar")
    mmap.close
  end

  it "should mmap when opening existing file" do
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    mmap.put_bytes("hello foo bar baz")
    expect(mmap.position).to eq(17)
    mmap.close
    mmap = Mmap::ByteBuffer.new(@path, 2048)
    expect(mmap.get_bytes(17)).to eq("hello foo bar baz")
    mmap.close
  end
end
