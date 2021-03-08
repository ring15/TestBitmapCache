package com.founq.testbitmapcache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by ring on 2021/3/5.
 */
public class ImageResize {

    /**
     * 用来优化图片
     *
     * @param context  上下文
     * @param id       图片id（如果是网络上获取，可能是string等）
     * @param maxW     最大的宽（不管给的多大，最大就只能是这么多）
     * @param maxH     最大的高（不管给的多大，最大就只能是这么多）
     * @param hasAlpha 是否需要透明度，如果不需要透明度，就去掉
     * @param reusable 可复用位置？
     * @return 优化后的图片
     */
    public static Bitmap resizeBitmap(Context context, int id, int maxW, int maxH, boolean hasAlpha, Bitmap reusable) {
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//将 解码的开关 打开
        BitmapFactory.decodeResource(resources, id, options);//这里不会去加载图片，只解码出相关参数信息，不耗内存，只获取宽高等信息
        int w = options.outWidth;
        int h = options.outHeight;//真实的宽高
        //设置缩放系数，官方要求，以2的倍数缩放（假如想从1024->80像素，要1024/2=512/2=256/2=128【/2=64】，
        // 从1024开始，以2的倍数缩小，128和64都是接近80的，都可以选择【根据项目需求】，一个缩小8倍，一个缩小16倍）
        //差值让系统自己去处理
        options.inSampleSize = calculateInSampleSize(w, h, maxW, maxH);//设置缩放系数
        if (!hasAlpha) {
            //Config有几种类型，都什么意义呢
            //ARGB_8888： 系统默认使用，一个像素点有四个通道的颜色，A,R,G,B各占8个位的精度，所以一个像素占4个字节的内存，一个字节是8位，所以，32位
            //ALPHA_8：  每个像素都需要1（8位）个字节的内存
            //HARDWARE：
            //RGB_565：腾讯等默认都会使用RGB_565，写在了他们的开发规范里的，如果不需要透明度的情况下；R占5位精度，G占6位精度，B占5位精度，一共是16位精度，折合两个字节
            //RGBA_F16：
            //ARGB_4444： 被遗弃了，A(Alpha)占4位的精度，R(Red)占4位的精度，G(Green)占4位的精度，B（Blue）占4位的精度，加起来一共是16位的精度，折合是2个字节
            options.inPreferredConfig = Bitmap.Config.RGB_565;//没有透明度，会减少一半的内存，从32位到16位
        }
        options.inJustDecodeBounds = false;//将 解码的开关 关闭//关闭后才会加载图片，解码不关闭，就是获取信息而已

        //设置成能复用
        options.inMutable = true;
        //设置复用需要的内存位置
        options.inBitmap = reusable;//图片复用的原理就是加载同一个内存块【两个指针指向同一个堆内存】

        return BitmapFactory.decodeResource(resources, id, options);
    }

    /**
     * 计算缩放系数
     *
     * @param w    图片真实宽度
     * @param h    图片真实高度
     * @param maxW 最大的宽
     * @param maxH 最大的高
     * @return 缩放系数
     */
    private static int calculateInSampleSize(int w, int h, int maxW, int maxH) {
        int inSampleSize = 1;
        if (w > maxW && h > maxH) {
            inSampleSize = 2;
            while (w / inSampleSize > maxW && h / inSampleSize > maxH) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
