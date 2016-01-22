# encoding: utf-8

require "java"
require "jruby-mmap/version"

# local dev setup
classes_dir = File.expand_path("../../../build/classes/main", __FILE__)

if File.directory?(classes_dir)
  # if in local dev setup, add target to classpath
  $CLASSPATH << classes_dir unless $CLASSPATH.include?(classes_dir)
else
  # otherwise use included jar
  require "jruby-mmap/jruby-mmap-#{Mmap::VERSION}.jar"
end

require "mmap"
