require 'rubygems'
require 'rubygems/dependency_installer'

RETRY_LIMIT = 3

def require_or_install name
  $__retry_count__ = 0
  begin
    require name
  rescue LoadError
    puts "ERROR: no #{name}\n"
    Gem::DependencyInstaller.new.install name
    if $__retry_count__ <= RETRY_LIMIT
      $__retry_count__ = $__retry_count__ + 1
      puts "retrying require..."
      retry
    end
  end
end

require_or_install 'openssl'
require_or_install 'base64'
require_or_install 'nokogiri'
