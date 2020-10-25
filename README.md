# CacheContrast
**DiskLruCache**和**BlobCache**对**bitmap**的写入本地和从本地读取的速度对比

[简书博客](https://www.jianshu.com/p/f5b50c82708b)

[CSDN博客](https://blog.csdn.net/hewuzhao/article/details/108696808)

注意：所有测试数据是基于Android Studio自带模拟器的Pixel XL机器测试。


### 保存Bitmap到本地


<img src="https://github.com/hewuzhao/CacheContrast/blob/master/image/%E5%AD%98%E5%82%A8%E6%97%B6%E9%97%B4%E5%AF%B9%E6%AF%94.png" width="500" height="309"/><br/>


### 读取Bitmap

<img src="https://github.com/hewuzhao/CacheContrast/blob/master/image/%E8%AF%BB%E5%8F%96%E6%97%B6%E9%97%B4%E5%AF%B9%E6%AF%94.png" width="500" height="309"/><br/>


**总结**

从上面的对比结果可以看得出：

1. BlobCache在存储速度上比DiskLruCache慢；

2. BlobCache在读取速度上比DiskLruCache快；


