# SpringBoot——瑞吉外卖笔记

## 一、登录、退出功能

### 1、设置静态资源映射

（1）SpringBoot默认的静态资源映射：

​         只要静态资源放在类路径下： called `/static` (or `/public` or `/resources` or `/META-INF/resources`

（2）资源请求的匹配的执行顺序：

​         请求进来，先去找Controller看能不能处理。不能处理的所有请求又都交给静态资源处理器。静态资源也找    		 不到则响应404页面

（3）通过修改配置文件自定义静态资源映射路径：

（4）通过配置类来自己定义静态资源映射：

````java
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }
}
````

## 二、用户信息管理功能

### 1、设置登录判断过滤器

````java
@WebFilter(filterName="loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的路径
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String []urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //判断是否需要处理本次请求
        boolean check = check(urls,requestURI);
        //如果不需要处理，则直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态,如果以登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            filterChain.doFilter(request,response);
            return;
        }
        //如果未登录，则返回json信息
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGING")));
    }
    public boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return match;
            }
        }
        return false;
    }
}
````

并在主程序上添加注解@ServletComponentScan

```java
@ServletComponentScan(basePackages = "com.bbu.reggie.filter")
```

### 2、设置全局异常处理

用来管理当哪些类出现异常时，用此管理器统一进行管理，减少代码的冗余量

````java
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String []split = message.split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
````

### 3、配置mybatisPlus分页功能

当使用MybatisPlus的分页功能时，要自定义一个配置类：

````java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}
````

### 4、配置消息转换器

由于员工数据的id是long类型，并且采用的是雪花算法生成的19位数字，由于浏览器在存储整型数据时，只能精确到前16位，因此后三位的精度丢失，并用0代替。因此，我们需要配置消息转换器，将java对象装换为json对象时，将id的类型装换为字符串类型。

配置的消息装换器如下：

````java
//配置在此类中
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

@Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jason将java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将此消息转换器对象添加到mvc框架的消息转换器的集合中
        converters.add(0,messageConverter);
    }
}
````

其中的JacksonObjectMapper 类为：自定义的类



