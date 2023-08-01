resource "aws_ecs_cluster" "statistics_server_cluster" {
  name = "statistics-server-${terraform.workspace}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}


resource "aws_ecs_task_definition" "statistics_server_task_definition" {
  family                   = "statistics-server-family-${terraform.workspace}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 1024 // 1 vCPU = 1024 CPU units
  memory                   = 2048
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = templatefile("./templates/ecs/statistics_server_app.json.tpl", {
    app_image      = aws_ecr_repository.statistics_server.repository_url
    app_port       = 8080
    fargate_cpu    = 1024
    fargate_memory = 2048
    aws_region     = var.aws_region
    spring_profile = terraform.workspace
    app_id         = data.aws_ssm_parameter.statistics_app_id.value
    db_port        = "3306"
    db_host        = aws_db_instance.dumped_db.address
    db_name        = "wca_development"
    db_username    = data.aws_ssm_parameter.dumped_db_read_user.value
    db_password    = data.aws_ssm_parameter.dumped_db_read_password.value
  })
}

resource "aws_ecs_service" "statistics_server_service" {
  name            = "statistics-server-service-${terraform.workspace}"
  cluster         = aws_ecs_cluster.statistics_server_cluster.id
  task_definition = aws_ecs_task_definition.statistics_server_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_default_subnet.default_az1.id, aws_default_subnet.default_az2.id]
    security_groups  = [aws_security_group.http_security_group.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_alb_target_group.statistics_server_target_group.id
    container_name   = "statistics-server-app"
    container_port   = var.default_tomcat_port
  }

  deployment_controller {
    type = "CODE_DEPLOY"
  }

  #   depends_on = [aws_iam_role_policy_attachment.ecs_task_execution_role]
}
