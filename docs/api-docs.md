# 接口文档约定

文档以 **springdoc OpenAPI 注解**为唯一来源，Apifox 通过 URL 定时导入，Swagger UI 只作为备用查看工具。

## 地址

| 用途 | 地址 |
|---|---|
| Apifox 导入 / 机器读取 | `http://localhost:8094/v3/api-docs` |
| Swagger UI 备用查看 | `http://localhost:8094/swagger-ui.html` |

远程环境把 `localhost:8094` 换成对应环境地址即可。两个地址都已排除登录拦截，不需要 `accessToken`。

## 注解规范

统一使用 OpenAPI 3 注解（`io.swagger.v3.oas.annotations.*`），不要使用 Swagger 2 的 `@Api`、`@ApiOperation`。

### Controller

```java
@Tag(name = "用户管理", description = "用户查询、创建、更新、删除")
@RestController
public class UserController {

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/users/{id}")
    public UserVO get(@Parameter(description = "用户 ID") @PathVariable Long id) {
        ...
    }
}
```

- 每个 Controller 一个 `@Tag`，`name` 用中文模块名
- 每个接口一个 `@Operation`，`summary` 写清楚做什么
- 路径参数、查询参数用 `@Parameter(description = "...")`
- 请求体参数说明写在 DTO 的 `@Schema` 上

### DTO / VO

```java
@Schema(description = "创建用户请求参数")
@Data
public class UserCreateDTO {

    @Schema(description = "用户名", example = "zhangsan")
    private String username;
}
```

- 类上 `@Schema(description = ...)`
- 字段上补 `description`，必要时补 `example`
- 新增字段必须补 `@Schema`，否则 Apifox 里看不到含义

### 统一响应

运行时所有接口都会被 `GlobalResponseAdvice` 包装成：

```json
{ "code": 200, "message": "成功", "data": { } }
```

Swagger / Apifox 中展示的是 `data` 里的业务对象（如 `UserVO`），联调时注意外层还有 `code`、`message`、`data` 三层。

## Apifox 导入

1. 打开 Apifox 项目 → 项目设置 → 导入数据 → OpenAPI/Swagger
2. 选择「URL 导入」，填 `http://localhost:8094/v3/api-docs`
3. 建议勾选：
   - 导入 Servers 为环境
   - 导入 Security Scheme（会识别 `accessToken` 请求头）
4. 需要持续同步时开启定时导入（最小间隔 30 分钟）

Apifox 支持 OpenAPI 3.0 和 3.1，springdoc 3.x 默认输出 3.1，可直接导入。

## 新增接口检查清单

- [ ] Controller 方法有 `@Operation(summary = "...")`
- [ ] 所属 Controller 有 `@Tag`
- [ ] 路径 / 查询参数有 `@Parameter`
- [ ] 请求 DTO、响应 VO 的字段有 `@Schema`
- [ ] 启动后访问 `/v3/api-docs` 能看到该接口
- [ ] 在 Apifox 里执行一次定时导入或手动导入

## 注意

- Swagger 相关路径已在 `WebConfig` 排除登录拦截
- `GlobalResponseAdvice` 对 `/v3/api-docs` 做了放行，不会包装文档 JSON
- 生产环境如果不想暴露文档，可在 `application-prod.yml` 设置 `springdoc.api-docs.enabled: false` 和 `springdoc.swagger-ui.enabled: false`
