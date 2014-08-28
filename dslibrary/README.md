ds_library
==============

#eventbus 2.2.0
https://github.com/greenrobot/EventBus

-keepclassmembers class ** {
    public void onEvent*(**);
}

修改记录
2014-08-26
1.参照square/otto将Map<Class<?>, Object> stickyEvents改写为Map<Class<?>, Set<Object>> stickyEvents;
可以缓存多个事件
2.添加方法addEvent



=================

add library bufferknife

-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

=============


