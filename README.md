#### application.yml 文件存放位置的加载优先级
> 1. 每个位置的application.yml文件都会被加载，如果是相同的配置，例如都配置了端口，那么高优先级覆盖低优先级;
> 2. 如果是不同的配置，则会互补;

**(application.yml或者application.properties)优先级顺序是：**
项目根目录/config > 项目根目录 > 项目根目录/classpath/resources/config > 项目根目录/classpath/resources

**我们也可以通过配置spring.config.location来改变默认配置**
****
