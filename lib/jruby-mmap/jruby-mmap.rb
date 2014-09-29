# encoding: utf-8

require "java"

classes = File.expand_path("../../../target/classes", __FILE__)
$CLASSPATH << classes unless $CLASSPATH.include?(classes)

require "mmap"