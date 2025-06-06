# 在线考试系统

## 项目介绍

本项目是一个完整的在线考试系统，采用前后端分离架构设计，为教育机构提供便捷的在线考试解决方案。系统支持多角色管理（管理员、学生），提供考试创建、题库管理、在线答题、自动评分等功能。

项目测试地址：http://120.27.246.217:5173/



## 技术栈



### 前端

- **框架**: Vue 3 + Vite

- **UI组件库**: Element Plus

- **路由**: Vue Router

- **HTTP客户端**: Axios

  

### 后端

- **框架**: Spring Boot 3.1.5

- **安全**: Spring Security + JWT

- **数据库**: MySQL 8

- **构建工具**: Maven

  

  

## 功能特性

### 管理员功能



- 用户管理

- 考试管理（创建、编辑、发布）

- 题库管理

- 成绩统计

- 系统设置

  

### 学生功能

- 个人信息管理

- 查看可参加的考试

- 在线答题

  

  

## 项目结构

```
exam-system/
├── exam-system-frontend/    # 前端项目
│   ├── public/              # 静态资源
│   ├── src/                 # 源代码
│   │   ├── api/             # API接口
│   │   ├── assets/          # 资源文件
│   │   ├── components/      # 公共组件
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # Pinia状态管理
│   │   ├── utils/           # 工具函数
│   │   ├── views/           # 页面组件
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   ├── index.html           # HTML模板
│   ├── vite.config.js       # Vite配置
│   └── package.json         # 依赖配置
│
└── exam-system-backend/     # 后端项目
    ├── src/
    │   ├── main/
    │   │   ├── java/com/wzu/exam/
    │   │   │   ├── config/       # 配置类
    │   │   │   ├── controller/   # 控制器
    │   │   │   ├── model/        # 实体类
    │   │   │   ├── repository/   # 数据访问层
    │   │   │   ├── service/      # 业务逻辑层
    │   │   │   ├── security/     # 安全相关
    │   │   │   └── util/         # 工具类
    │   │   └── resources/
    │   │       ├── application.properties  # 应用配置
    │   │       └── data.sql                # 初始化数据
    │   └── test/                           # 测试代码
    └── pom.xml                             # Maven配置
```

## 安装与运行

### 前端

```bash
# 进入前端目录
cd exam-system-frontend

# 安装依赖
npm install

# 开发环境运行
npm run dev

# 构建生产环境
npm run build
```

### 后端

```bash
# 进入后端目录
cd exam-system-backend

# 使用Maven构建
./mvnw clean package

# 运行应用
java -jar target/exam-0.0.1-SNAPSHOT.jar
```

## 数据库配置

系统默认使用MySQL数据库，配置信息在`application.properties`文件中：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/exam_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456
```

首次运行时，系统会自动创建所需的表结构，并初始化基础数据。

## 默认账户

系统初始化后提供以下默认账户：

- 管理员账户：admin / 123456
- 学生账户：student1 / 123456

## 浏览器兼容性

- Chrome (推荐)
- Firefox
- Edge
- Safari

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱：2918636547@qq.com

  
