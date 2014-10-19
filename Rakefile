require "bundler/setup"

begin
  require 'ant'
rescue
  puts("error: unable to load Ant, make sure Ant is installed, in your PATH and $ANT_HOME is defined properly")
  puts("\nerror details:\n#{$!}")
  exit(1)
end


desc "run specs"
task :spec do
  require 'rspec/core/rake_task'
  RSpec::Core::RakeTask.new
end

task :setup do
  ant.mkdir 'dir' => "target/classes"
  ant.path 'id' => 'classpath' do
    fileset 'dir' => "target/classes"
  end
end

desc "compile JRuby and Java proxy classes"
task :build => [:setup] do |t, args|
  require 'jruby/jrubyc'
  ant.javac(
    'srcdir' => "src/",
    'destdir' => "target/classes/",
    'classpathref' => 'classpath',
    'debug' => "yes",
    'includeantruntime' => "no",
    'verbose' => false,
    'listfiles' => true
  ) {}
end
