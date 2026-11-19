# 统一响应设计

## 目标

所有接口（含异常、拦截器）返回一致的 `{code, message, data}` 结构；Controller 直接返回业务对象。

## 设计

### 响应结构

```json
{ "code": 200, "message": "success", "data": {} }
```

### 错误码

`result/ResultCode` 枚举：SUCCESS(200)、BAD_REQUEST(400)、UNAUTHORIZED(401)、FORBIDDEN(403)、NOT_FOUND(404)、METHOD_NOT_ALLOWED(405)、INTERNAL_ERROR(500)。

### 全局包装

`result/GlobalResponseAdvice` 实现 `ResponseBodyAdvice<Object>`：

- 已是 `Result` 的 body 原样返回，避免二次包装，并按其 code 同步 HTTP 状态
- 普通对象、List、null（void 方法）包装为 `Result.ok(body)`，HTTP 状态 200
- `String` 返回值使用 ObjectMapper 序列化为 JSON，并强制 `application/json`
- 返回类型为 `ResponseEntity` 时不覆盖其显式状态码
- 跳过 `Resource`、`byte[]`，避免破坏文件下载

### Controller

去掉手写 `Result`，直接返回业务对象；`logout` 返回 void。

### 异常与 HTTP 状态

`GlobalExceptionHandler` 返回 `ResponseEntity<Result<Void>>`，HTTP 状态与 body.code 一致：

- `BusinessException`：按自身 code；非标准 HTTP 码回落 400
- 参数绑定/校验/请求体不可读：400
- 方法不支持：405；静态资源不存在：404
- 其他异常：500

`AuthInterceptor` 返回 401 + `ResultCode.UNAUTHORIZED`。

### 测试

补充 `spring-boot-starter-test`，新增不依赖 DB/Redis 的单元测试覆盖 Result、Advice、异常映射。
