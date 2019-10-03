# DynamicProxyDemo
A serious of my proxy demo

为帮助自己理解动态代理（聚合方式）

而尝试自己模拟实现，于是就写了这个demo

# classentity:

对class、method、field做了一层抽象，并重写toString方法，方便提取java模板文件

# Services:

代理的目标接口和目标对象（代理对象默认生成在目标对象所在路径）

# proxy:

代理工厂

# proxy2:

通过实现Handler接口完成代理，用户可以自定义代理逻辑

# Main.java:

流程演示


水平有限，请见谅