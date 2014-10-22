# encoding: utf-8

require "java"

# local dev setup
classes_dir = File.expand_path("../../../target/classes", __FILE__)

if File.directory?(classes_dir)
  # if in local dev setup, add target to classpath
  $CLASSPATH << classes_dir unless $CLASSPATH.include?(classes_dir)
else
  # otherwise use included jar
  require "jruby-mmap/jruby-mmap.jar"
end

require "mmap"