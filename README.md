# JRuby MMap

This gem only supports [JRuby](http://jruby.org/).

JRuby MMap is a Java JRuby extension wrapper over the Java NIO memory mapping. 

See also
- [JRuby MMap Queues](https://github.com/colinsurprenant/jruby-mmap-queues)
- [JRuby MMap Benchmarks](https://github.com/colinsurprenant/jruby-mmap-benchmark)

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
FILE_PATH = "mmap-file-path"

mmap = Mmap::ByteBuffer.new(FILE_PATH, BYTE_SIZE)
mmap.put_bytes("foobar")
mmap.close
```

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

*JRuby MMap* is released under the Apache License, Version 2.0. See [LICENSE](https://github.com/colinsurprenant/jruby-mmap/blob/master/LICENSE).
