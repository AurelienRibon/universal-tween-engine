![](http://www.aurelienribon.com/blog/wp-content/uploads/2012/05/tween-engine-big-logo.jpg)

## Check out the demo!

  * [Android application](https://play.google.com/store/apps/details?id=aurelienribon.tweenengine.demo)
  * [Desktop application](http://code.google.com/p/java-universal-tween-engine/downloads/detail?name=tween-engine-demo-6.3.0.zip)
  * [WebGL html5 page](http://www.aurelienribon.com/universal-tween-engine/gwt/demo.html) (requires a WebGL enabled browser)

## Introduction

The Universal Tween Engine enables the interpolation of every attribute from any object in any Java project (being Swing, SWT, OpenGL or even Console-based). Implement the TweenAccessor interface, register it to the engine, and animate anything you want!

In one line, send your objects to another position (here x=20 and y=30), with a smooth elastic transition, during 1 second).

```java
// Arguments are (1) the target, (2) the type of interpolation, 
// and (3) the duration in seconds. Additional methods specify  
// the target values, and the easing function. 

Tween.to(mySprite, Type.POSITION_XY, 1.0f).target(20, 30).ease(Elastic.INOUT);

// Possibilities are:

Tween.to(...); // interpolates from the current values to the targets
Tween.from(...); // interpolates from the given values to the current ones
Tween.set(...); // apply the target values without animation (useful with a delay)
Tween.call(...); // calls a method (useful with a delay)

// Current options are:

myTween.delay(0.5f);
myTween.repeat(2, 0.5f);
myTween.repeatYoyo(2, 0.5f);
myTween.pause();
myTween.resume();
myTween.setCallback(callback);
myTween.setCallbackTriggers(flags);
myTween.setUserData(obj);

// You can of course chain everything:

Tween.to(...).delay(1.0f).repeat(2, 0.5f).start(myManager);

// Moreover, slow-motion, fast-motion and reverse play is easy,
// you just need to change the speed of the update:

myManager.update(delta * speed);
```

Create some powerful animation sequences!

```java
Timeline.createSequence()
    // First, set all objects to their initial positions
    .push(Tween.set(...))
    .push(Tween.set(...))
    .push(Tween.set(...))

    // Wait 1s
    .pushPause(1.0f)

    // Move the objects around, one after the other
    .push(Tween.to(...))
    .push(Tween.to(...))
    .push(Tween.to(...))

    // Then, move the objects around at the same time
    .beginParallel()
        .push(Tween.to(...))
        .push(Tween.to(...))
        .push(Tween.to(...))
    .end()

    // And repeat the whole sequence 2 times
    // with a 0.5s pause between each iteration
    .repeatYoyo(2, 0.5f)

    // Let's go!
    .start(myManager);
```

You can also quickly create timers:

```java
Tween.call(myCallback).delay(3000).start(myManager);
```

Main features are:

  * Supports every interpolation function defined by [Robert Penner](http://www.robertpenner.com/easing/).
  * Can be used with any object. You just have to implement the TweenAccessor interface when you want interpolation capacities.
  * Every attribute can be interpolated. The only requirement is that what you want to interpolate can be represented as a float number.
  * One line is sufficient to create and start a simple interpolation.
  * Delays can be specified, to trigger the interpolation only after some time.
  * Many callbacks can be specified (when tweens complete, start, end, etc.).
  * Tweens and Timelines are pooled by default. If enabled, there won't be any object allocation during runtime! You can safely use it in Android game development without fearing the garbage collector.
  * Tweens can be sequenced when used in Timelines.
  * Tweens can act on more than one value at a time, so a single tween can change the whole position (X and Y) of a sprite for instance !
  * Tweens and Timelines can be repeated, with a yoyo style option.
  * Simple timers can be built with Tween.call().
  * **Source code extensively documented!**

## Get started and documentation index

Detailed documentation with code snippets and examples is available for the following topics:
  * Get started --- A step-by-step example to get you started, with code

  * The TweenAccessor interface --- Know how to implement it
  * Tweens and options --- See what are the possibilities
  * Timelines and options --- Learn how to build powerful sequences
  * Animating Android apps --- See how to use the engine with Android UIs

## Where can I ask for help?

There is a dedicated forum for you:
http://www.aurelienribon.com/forum/viewforum.php?f=5

Also, the following link will guide you to a discussion thread that started it all:  
http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=494