require 'rubygems'
require 'rubygems/dependency_installer'

# These are our known dependencies.
dependencies = ['openssl', 'base64', 'isorelax', 'nokogiri']

# We will load and then attempt to require to check module load.
# We limit this to three attempts however.
RETRY_LIMIT = 3

# Module installer.
def require_or_install name
  $__retry_count__ = 0
  begin
    require name
  rescue LoadError => err
    puts "...loading #{name}..."
    Gem::DependencyInstaller.new.install name
    puts "done.\n"
    if $__retry_count__ <= RETRY_LIMIT
      $__retry_count__ = $__retry_count__ + 1
      puts "retrying require..."
      retry
    end
  end
end

# Checking dependencies.
dependencies.each { |dependency| require_or_install dependency }
