#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'mobsms'
  s.version          = '1.0.1'
  s.summary          = 'SMSSDK Plugin Project.'
  s.description      = <<-DESC
The smssdk fundation project.
                       DESC
  s.homepage         = 'https://www.mob.com/mobService/sms'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Mob' => 'mobproducts@mob.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'mob_smssdk'
  s.dependency 'mob_plat'
  s.static_framework = true
  s.ios.deployment_target = '8.0'
  
end

