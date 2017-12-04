# SnakeViewMaker

 SnakeViewMaker 是一个模仿即刻App里用户头像拖动效果的工具类。

![image](https://github.com/devilist/SnakeViewMaker/raw/master/images/snake_shootcut.gif)

# 调用方法：

1.创建 SnakeViewMaker；

```
    SnakeViewMaker snakeViewMaker = new SnakeViewMaker(MainActivity.this);
```

2.绑定

```
    snakeViewMaker
    .addTargetView(imageView)                                  // 绑定目标View
    .attachToRootLayout((ViewGroup) findViewById(R.id.root));  // 绑定Activity/Fragment的根布局
```

3.其他相关API

```
    snakeViewMaker.detachSnake();                // 解除绑定
    snakeViewMaker.updateSnakeImage();           // 当目标View的视图发生变化时，调用此方法用以更新Snake视图状态
    snakeViewMaker.interceptTouchEvent(true);    // Snake拖动过程中是否需要屏蔽其他onTouch事件，默认屏蔽
    snakeViewMaker.setVisibility(View.VISIBLE);  // 控制可见性
    snakeViewMaker.setClickable(true);           // 控制可点击
    snakeViewMaker.setEnabled(true);             // 控制可触摸
```

# 注意事项

    1.目前不支持LinearLayout根布局
    2.本地图片可直接调用。网络图片需要在图片加载完成后才能调用，不然可能出现绑定不成功的情况
    例如，用glide加载网络图片时，调用时机如下：

```
    snakeViewMaker = new SnakeViewMaker(MainActivity.this);
    Glide.with(this).load(url).asBitmap()
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                            snakeViewMaker.addTargetView(imageView)
                                    .attachToRootLayout((ViewGroup) findViewById(R.id.root));
                        }
                    });
```