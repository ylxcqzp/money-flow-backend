# Money Flow API 文档

本文档描述了 Money Flow 前端项目所需的后端接口规范。
所有接口均基于 RESTful 风格。
基础 URL: `/api` (可配置)

## 通用说明

### 鉴权
所有受保护的接口需要在 Request Header 中携带 JWT Token：
`Authorization: Bearer <token>`

### 响应格式
建议采用统一响应结构：
```json
{
  "code": 0,      // 0 表示成功，非 0 表示业务错误  
  "msg": "success", // 提示信息
  "data": { ... }   // 业务数据
}
```
*注：前端 request 拦截器目前会直接返回 response.data，请确保后端返回的数据结构符合预期。如果直接返回数据对象也是可以的，前端代码已做简单兼容。建议为了扩展性使用上述包装结构。*

---

## 1. 认证 (Auth)

### 1.1 用户登录
- **URL**: `/auth/login`
- **Method**: `POST`
- **描述**: 邮箱密码登录
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "jwt-token-string",
    "user": {
      "id": 1,
      "username": "User",
      "email": "user@example.com",
      "avatar_url": "..."
    }
  }
  ```

### 1.2 用户注册
- **URL**: `/auth/register`
- **Method**: `POST`
- **描述**: 注册新用户
- **Request Body**:
  ```json
  {
    "username": "Nickname",
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response**: 同登录接口，返回 token 和用户信息。

### 1.3 获取当前用户信息
- **URL**: `/auth/me`
- **Method**: `GET`
- **Response**:
  ```json
  {
    "user": { ... }
  }
  ```

### 1.4 退出登录
- **URL**: `/auth/logout`
- **Method**: `POST`
- **描述**: 清除服务端 Session 或记录 Token 失效（可选）。
- **Response**:
  ```json
  {
    "code": 200,
    "msg": "success"
  }
  ```

---

## 2. 账户 (Account)

### 2.1 获取账户列表
- **URL**: `/accounts`
- **Method**: `GET`
- **描述**: 获取当前用户的所有资产账户，包含实时余额。
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "现金",
      "type": "cash", // cash, card, alipay, wechat, other
      "icon": "Wallet",
      "initialBalance": 1000.00,
      "currentBalance": 1500.00, // 后端需计算当前余额
      "sortOrder": 0
    }
  ]
  ```

### 2.2 创建账户
- **URL**: `/accounts`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "name": "招商银行",
    "type": "card",
    "icon": "CreditCard",
    "initialBalance": 5000.00
  }
  ```

### 2.3 更新账户
- **URL**: `/accounts/:id`
- **Method**: `PUT`
- **Request Body**: 需更新的字段

### 2.4 删除账户
- **URL**: `/accounts/:id`
- **Method**: `DELETE`

---

## 3. 分类 (Category)

### 3.1 获取分类列表
- **URL**: `/categories`
- **Method**: `GET`
- **Query Params**:
  - `type`: `expense` | `income` (可选，不传则返回所有)
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "餐饮",
      "type": "expense",
      "icon": "Utensils",
      "parentId": 0, // 0 或 null 表示一级分类
      "children": [ ... ] // 树形结构
    }
  ]
  ```

### 3.2 创建分类
- **URL**: `/categories`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "name": "夜宵",
    "type": "expense",
    "icon": "Food",
    "parentId": 1
  }
  ```

### 3.3 更新分类
- **URL**: `/categories/:id`
- **Method**: `PUT`

### 3.4 删除分类
- **URL**: `/categories/:id`
- **Method**: `DELETE`

---

## 4. 交易 (Transaction)

### 4.1 获取交易列表
- **URL**: `/transactions`
- **Method**: `GET`
- **Query Params**:
  - `startDate`: `2026-01-01`
  - `endDate`: `2026-01-31`
  - `type`: `expense` | `income` | `transfer` | `all`
  - `categoryId`: (可选)
  - `accountId`: (可选)
  - `tags`: (可选，逗号分隔)
- **Response**:
  ```json
  [
    {
      "id": 1001,
      "type": "expense",
      "amount": 50.00,
      "date": "2026-02-04",
      "categoryId": 10,
      "subCategoryId": 11,
      "accountId": 1,
      "note": "午餐",
      "tags": "工作餐,外卖" // 建议返回数组或逗号分隔字符串
    }
  ]
  ```

### 4.2 创建交易
- **URL**: `/transactions`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "type": "expense",
    "amount": 50.00,
    "date": "2026-02-04",
    "categoryId": 10,
    "accountId": 1,
    "note": "备注",
    "tags": ["tag1", "tag2"]
  }
  ```

### 4.3 更新交易
- **URL**: `/transactions/:id`
- **Method**: `PUT`

### 4.4 删除交易
- **URL**: `/transactions/:id`
- **Method**: `DELETE`

---

## 5. 预算 (Budget)

### 5.1 获取月度预算
- **URL**: `/budgets`
- **Method**: `GET`
- **Query Params**:
  - `month`: `2026-02`
- **Response**:
  ```json
  {
    "month": "2026-02",
    "total": 5000.00,
    "categories": {
      "10": 1000.00, // categoryId: amount
      "20": 500.00
    }
  }
  ```

### 5.2 设置/更新预算
- **URL**: `/budgets`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "month": "2026-02",
    "total": 5000.00,
    "categories": {
      "10": 1000.00
    }
  }
  ```

---

## 6. 周期性规则 (Recurring Rule)

### 6.1 获取规则列表
- **URL**: `/recurring-rules`
- **Method**: `GET`

### 6.2 创建规则
- **URL**: `/recurring-rules`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "type": "expense",
    "amount": 3000,
    "frequency": "monthly", // daily, weekly, monthly, yearly
    "startDate": "2026-03-01",
    "categoryId": 20,
    "accountId": 1,
    "description": "房租"
  }
  ```

### 6.3 删除规则
- **URL**: `/recurring-rules/:id`
- **Method**: `DELETE`

### 6.4 生成周期性交易
- **URL**: `/recurring-rules/generate`
- **Method**: `POST`
- **描述**: 触发后端检查所有周期性规则，自动生成符合条件的交易记录
- **Response**:
  ```json
  {
    "code": 200,
    "msg": "success",
    "data": {
      "generated": 5, // 生成的交易数量
      "transactions": [ ... ] // 生成的交易列表
    }
  }
  ```

---

## 7. 储蓄目标 (Goal)

### 7.1 获取目标列表
- **URL**: `/goals`
- **Method**: `GET`

### 7.2 创建目标
- **URL**: `/goals`
- **Method**: `POST`

### 7.3 更新目标
- **URL**: `/goals/:id`
- **Method**: `PUT`

### 7.4 删除目标
- **URL**: `/goals/:id`
- **Method**: `DELETE`

### 7.5 存取款记录
- **URL**: `/goals/:id/records`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "amount": 1000,
    "operateDate": "2026-02-04"
  }
  ```
