cmake_minimum_required(VERSION 3.4.1)

set (ENABLE_SDL2 ON)

set(OPENGL_LOCATION ${CMAKE_SOURCE_DIR}/gl4es)
set(OPENGL_INCLUDE_DIR ${OPENGL_LOCATION}/src/main/jni/gl4es/include)
set(OPENGL_opengl_LIBRARY ${OPENGL_LOCATION}/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libGL.so)
set(OPENGL_glx_LIBRARY ${OPENGL_LOCATION}/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib/${ANDROID_ABI}/libGL.so)

add_subdirectory("${CMAKE_SOURCE_DIR}/app/src/main/jni/systemshock")