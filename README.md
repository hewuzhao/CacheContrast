# CacheContrast
DiskLruCache和BlobCache对bitmap的写入本地和从本地读取的性能对比


### 保存Bitmap到本地
保存大图（1M左右）

<img src="https://github.com/hewuzhao/CacheContrast/blob/master/image/%E5%A4%A7%E5%9B%BE%E4%BF%9D%E5%AD%98%E5%AF%B9%E6%AF%94.jpg" width="270" height="480"/><br/>

保存小图（130k左右）

<img src="https://github.com/hewuzhao/CacheContrast/blob/master/image/%E5%B0%8F%E5%9B%BE%E4%BF%9D%E5%AD%98%E5%AF%B9%E6%AF%94.jpg" width="270" height="480"/><br/>


**总结：
不管是大图还是小图，总体来看BlobCache保存到本地的速度比DiskLruCache稍微快一点**


### 读取Bitmap
读取大图（1M左右）

<img src="https://github.com/hewuzhao/CacheContrast/blob/master/image/%E5%A4%A7%E5%9B%BE%E8%AF%BB%E5%8F%96%E5%AF%B9%E6%AF%94.jpg" width="270" height="480"/><br/>

读取小图（130k左右）

<img src="https://github.com/hewuzhao/CacheContrast/blob/master/image/%E5%B0%8F%E5%9B%BE%E8%AF%BB%E5%8F%96%E5%AF%B9%E6%AF%94.jpg" width="270" height="480"/><br/>

**总结**

**1. 大图情况下，读取速度：BlobCache > DiskLruCache > Android本地解码**
**BlobCache读取速度最快，Android的BitmapFactory.decodeResource最慢；**

**2. 小图情况下，BlobCache = Android本地解码 > DiskLruCache**
**BlobCache跟Android本地解码的速度几乎差不多，DiskLruCache最慢**



