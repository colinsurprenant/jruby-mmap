# encoding: utf-8

raise("JRuby required") unless defined?(JRUBY_VERSION)

Gem::Specification.new do |s|
  s.name = "jruby-mmap"
  s.version = "0.1.1"
  s.authors = ["Colin Surprenant"]
  s.date = Time.now.strftime('%Y-%m-%d')
  s.summary = "JRuby extension to Java NIO Mmap"
  s.description = s.summary
  s.email = ["colin.surprenant@gmail.com"]
  s.homepage = "http://github.com/colinsurprenant/jruby-mmap"
  s.require_paths = ["lib"]
  s.licenses = ["Apache-2.0"]
  s.platform = "java"
  s.files += `git ls-files`.lines.map(&:chomp)

  s.add_development_dependency "rspec"
  s.add_development_dependency "rake"
end