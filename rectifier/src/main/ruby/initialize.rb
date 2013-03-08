require 'rubygems'
require 'rubygems/dependency_installer'

def require_or_install name
  begin
    require name
  rescue LoadError
    puts "ERROR: no #{name}\n"
    Gem::DependencyInstaller.new.install name
  end
end

require_or_install 'openssl'
require_or_install 'base64'
require_or_install 'nokogiri'