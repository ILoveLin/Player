#TODO 设置构建本机库文件所需的 CMake的最小版本
cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_C_STANDARD 99)
add_library(yylRecord
        # TODO 标志库共享
        SHARED
        src/main/cpp/yyl_record.c
        src/main/cpp/yyl_log.c
        #   src/main/cpp/yyl_fixsurface.c
        # TODO C++源码文件的相对路径.
        )


# TODO 添加自己写的 C/C++源文件
add_library(vlc
        SHARED
        IMPORTED
        )
add_library(vlcjni
        SHARED
        IMPORTED
        )
message(AUTHOR_WARNING 'yyl CMAKE_ANDROID_ARCH_ABI=${CMAKE_ANDROID_ARCH_ABI})
message(AUTHOR_WARNING 'yyl CMAKE_SOURCE_DIR=${CMAKE_SOURCE_DIR})

set(soPath ${CMAKE_SOURCE_DIR}/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libvlc.so)
set(soPathjni ${CMAKE_SOURCE_DIR}/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libvlcjni.so)


# TODO ${CMAKE_SOURCE_DIR}：表示 CMakeLists.txt的当前文件夹路径
# TODO ${ANDROID_ABI}：编译时会自动根据 CPU架构去选择相应的库
# TODO ABI文件夹上面不要再分层，直接就 jniLibs/${ANDROID_ABI}/
# TODO ${CMAKE_SOURCE_DIR}/src/main/jniLibs/ffmpeg/${ANDROID_ABI}/libavcodec-57.so${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavcodec-57.so)


#message(AUTHOR_WARNING " jnilib=${soPathjni}")
#
# TODO 设置关联的 so 库名称、目标位置
# TODO ${CMAKE_SOURCE_DIR} 是 CMakeLists.txt 所在目录
set_target_properties(
        vlc
        PROPERTIES IMPORTED_LOCATION
        ${soPath}
)
set_target_properties(
        vlcjni
        PROPERTIES IMPORTED_LOCATION
        ${soPathjni}
)

include_directories(
        ${CMAKE_SOURCE_DIR}/include/
)
# TODO 引入第三方 .h 文件夹，也要添加到 CMakeLists.txt 文件中
# TODO include_directories( src/main/jniLibs/${ANDROID_ABI}/include )
# TODO 路径指向上面会编译出错(无法在jniLibs中引入)，指向下面的路径就没问题
# TODO include_directories( src/main/cpp/ffmpeg/include )



#TODO 将目标库与 NDK中的第三方库进行连接
target_link_libraries(yylRecord
        android
        log
        vlc
        vlcjni
        )
