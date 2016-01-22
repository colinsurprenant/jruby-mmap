# encoding: utf-8

lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'jruby-mmap/version'

raise("JRuby required") unless defined?(JRUBY_VERSION)

Gem::Specification.new do |s|
  s.name = "jruby-mmap"
  s.version = Mmap::VERSION

  s.authors = ["Colin Surprenant"]
  s.email = ["colin.surprenant@gmail.com"]

  s.summary = "JRuby extension to Java NIO Mmap"
  s.description = s.summary
  s.homepage = "http://github.com/colinsurprenant/jruby-mmap"
  s.licenses = ["Apache-2.0"]

  s.require_paths = ["lib"]
  s.files += Dir.glob(["jruby-mmap.gemspec", "lib/**/*.jar", "lib/**/*.rb", "spec/**/*.rb", "README.md", "LICENSE"])

  s.platform = "java"

  s.add_development_dependency "rspec", "~> 3"
  s.add_development_dependency "rake", "~> 10"
end
