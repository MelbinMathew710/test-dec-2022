provider "aws" {
  region = "us-east-1"  # Replace with your desired AWS region
}

resource "aws_s3_bucket" "deployment_bucket" {
  bucket = "spring-boot-lambda-deployment"
  acl    = "private"
}

resource "aws_s3_bucket_object" "application_jar" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "application.jar"
  source = "path/to/your/application.jar"
}

resource "aws_s3_bucket_object" "template_yaml" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "template.yaml"
  content = <<-EOF
    AWSTemplateFormatVersion: '2010-09-09'
    Transform: AWS::Serverless-2016-10-31

    Resources:
      SpringBootLambdaFunction:
        Type: AWS::Serverless::Function
        Properties:
          FunctionName: spring-boot-lambda
          Handler: com.example.Application::handleRequest
          Runtime: java11
          Timeout: 30
          MemorySize: 512
          CodeUri:
            Bucket: ${aws_s3_bucket.deployment_bucket.id}
            Key: application.jar
          Environment:
            Variables:
              SPRING_PROFILES_ACTIVE: production
              OTHER_ENV_VARIABLE: value
  EOF
}

resource "aws_cloudformation_stack" "lambda_stack" {
  name         = "spring-boot-lambda-stack"
  template_body = aws_s3_bucket_object.template_yaml.content

  capabilities = ["CAPABILITY_IAM"]
}
