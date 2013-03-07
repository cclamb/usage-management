require 'rubygems'

begin
  require 'openssl'
  require 'base64'
  require 'nokogiri'
rescue LoadError => err
  puts "ERROR: #{err.to_s}\n"
  raise
end
