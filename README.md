# JRuby Mmap

This gem only supports [JRuby](http://jruby.org/).

JRuby Mmap is a Java JRuby extension wrapper over the Java NIO memory mapping. 

See also
- [JRuby Mmap Queues](https://github.com/colinsurprenant/jruby-mmap-queues)
- [JRuby Mmap Benchmarks](https://github.com/colinsurprenant/jruby-mmap-benchmark)

## Installation

This gem only supports [JRuby](http://jruby.org/).

Add this line to your application's Gemfile:

```ruby
gem 'jruby-mmap'
```

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install jruby-mmap

## Building

Building uses Gradle. The build process compiles the Java classes, produces a jar file and copies the jar file in the project `lib/jruby-mmap` dir.

```sh
$ ./gradlew build
```

## Usage

```ruby
require "jruby-mmap"

BYTE_SIZE = 2048
FILE_PATH = "mmapfile.dat"

mmap = Mmap::ByteBuffer.new(FILE_PATH, BYTE_SIZE)
mmap.put_bytes("foobar")
mmap.close
```

### Non String copying put_bytes

In Ruby there's no concept of byte arrays, for IO, data is ultimately carried as strings. The `Mmap::ByteBuffer` class exposes two methods for 
writing bytes to the mmap byte buffer which takes a `String` as argument: `Mmap::ByteBuffer#put_bytes` and `Mmap::ByteBuffer#put_bytes_copy`. The former, `put_bytes` avoids 
copying the `String` content by directly passing the `String` underlying `ByteList` to the mmap byte buffer `put` method. This is 
obviously more efficient but also *unsafe* in the sense that further mutations of the `String` object will mutate that `ByteList` object and 
potentially create corruption in some way. Invoking `put_bytes` is typically the last operation performed on that string so in most cases it
should just be fine. If you are not sure or have doubts about the unsafe nature of `put_bytes` you can use `put_bytes_copy` which copies the 
string data into a new byte buffer and pass it to the mmap `put` method.

## Tests

```sh
$ bundle install
$ bundle exec rspec
```

## Author

**Colin Surprenant** on [GitHub](https://github.com/colinsurprenant) and [Twitter](https://twitter.com/colinsurprenant).

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/colinsurprenant/jruby-mmap.

## License and Copyright

*JRuby Mmap* is released under the Apache License, Version 2.0. See [LICENSE](https://github.com/colinsurprenant/jruby-mmap/blob/master/LICENSE).
