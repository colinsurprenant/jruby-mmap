# JRuby MMap

JRuby MMap is a Java JRuby extension wrapper over the Java NIO memory mapping. 

See also
- [JRuby MMap Queues](https://github.com/colinsurprenant/jruby-mmap-queues)
- [JRuby MMap Benchmarks](https://github.com/colinsurprenant/jruby-mmap-benchmark)

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

* [Colin Surprenant](https://github.com/colinsurprenant)

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## License and Copyright

*JRuby MMap* is released under the Apache License, Version 2.0. See [LICENSE](https://github.com/colinsurprenant/jruby-mmap/blob/master/LICENSE).
