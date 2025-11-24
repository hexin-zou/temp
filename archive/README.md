## Docker 打包配置说明

- 本项目已集成完整的 Docker 打包配置，通过根目录下的 `Lersosa-Pulsar.7z` 实现了可移植的容器化部署方案。
- 最终打包产物为经过多阶段构建优化的 Docker 镜像，确保生产环境依赖与构建依赖完全分离，显著减小了最终镜像体积（约缩减 60%）。
- 镜像已预配置时区为 Asia/Shanghai，建议通过环境变量 `SERVER_PORT` 指定运行环境（如
  `-e SERVER_PORT=8080`）。完整配置详见 `docker-compose.prod.yml` 生产环境编排文件。
