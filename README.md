![tundra](http://nathancorbyn.com/tundra.png)
---
`tundra` is a 3D game framework/engine built on top of LWJGL. Our motivation was to take what we built last year as `grassland` and advance it to make use of OpenGL's programmable pipeline. It didn't make sense to continue working on `grassland` as the use of fixed pipeline was pervasive in the code base. 

## Engine

Both `grassland` and `tundra` were built for use in game jams and therefore aspire to provide a small set of powerful abstractions to make game development as fast as possible. `tundra` has pushed this a long way with the introduction of callbacks into the engine making scripting timed behaviours trivial.

For example,

```java
after(1000, () -> {
  // Execute this after 1000 milliseconds
});

every(1000, () -> {
  // Execute this every 1000 milliseconds
});
```

Furthermore, a simple to use LERP utility is also provided,

```java
lerp(1000, f -> {
  // `f` will take all values between `start` and `end` over 1000 milliseconds
}, start, end);
```

## Physics

`tundra` also includes a small set of bindings into the JBullet physics engine. JBullet is performant and well put together; however, difficult to work with. Further work on `tundra` would definitely involve expanding these bindings to provide a complete interface.

## Graphics

`tundra` is yet to support animation; however, it moves far passed `grassland` in terms of graphics capability. `tundra` supports the loading and rendering of `.obj` files along with `.mtl` files.

Here is an example of a mesh rendered in `tundra` with several materials and lights,

![Rendering example](http://nathancorbyn.com/dredd.png)

The model used in this scene is available [here](https://sketchfab.com/models/74a05141476d4f6f8ebf83d9636923c5).

We don't yet support PBR, although it may take another rewrite before this is possible. We do however support normal, specular, diffuse, ambient and highlight mapping (all of which are used in the above).

## Feedback

Issue reports and feedback are welcome. Documentation should appear at some point in the future. Thanks for reading ^-^
