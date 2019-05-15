# RayTracer
***Java RayTracer***

Ray Tracer exercises from [Ray Tracer
Challenge](https://pragprog.com/book/jbtracer/the-ray-tracer-challenge) book.

library/ contains the ray tracing libraries.
projects/ contains the executable projects. Most projects output a single image to the images/
directory. See projects/build.gradle for the executable task names.

## Libraries
Raytracing code at [library/](tree/master/library).

## Tests
`gradle test` to run the unit tests.

## Chapter 1: Tuples, Points, and Vectors
### Project: TextRocketTracker
Code at [projects/chapter1/TextRocketTracker](tree/projects/chapter1/TextRocketTracker.java).
`gradle project:textrocket`

task canvasrocket(type:JavaExec) {
  main = 'projects.chapter2.CanvasRocketTracker'
task clock(type:JavaExec) {
  main = 'projects.chapter4.Clock'
task silhouette(type:JavaExec) {
  main = 'projects.chapter5.Silhouette'
task blueball(type:JavaExec) {
  main = 'projects.chapter6.BlueBall'
task sphere(type:JavaExec) {
  main = 'projects.chapter7.SphereScene'
task sphere2(type:JavaExec) {
  main = 'projects.chapter7.SphereScene2'
task plane(type:JavaExec) {
  main = 'projects.chapter9.PlaneScene'
task stripeballs(type:JavaExec) {
  main = 'projects.chapter10.StripeBalls'
task reflect(type:JavaExec) {
  main = 'projects.chapter11.ReflectScene'
task refract(type:JavaExec) {
  main = 'projects.chapter11.RefractScene'
task cubes(type:JavaExec) {
  main = 'projects.chapter12.CubeScene'
task cylinders(type:JavaExec) {
  main = 'projects.chapter13.CylinderScene'
task cones(type:JavaExec) {
  main = 'projects.chapter13.ConeScene'
task hexagon(type:JavaExec) {
  main = 'projects.chapter14.HexagonScene'
task teapot(type:JavaExec) {
  main = 'projects.chapter15.TeapotScene'
task csg(type:JavaExec) {
  main = 'projects.chapter16.CsgScene'
task rhc(type:JavaExec) {
  main = 'projects.chapter16.RoundHoleCubeScene'
task cover(type:JavaExec) {
  main = 'projects.appendix1.CoverWorld'
