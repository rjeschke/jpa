/*
 * defines.h
 *
 *  Created on: Feb 5, 2013
 *      Author: rjeschke
 */

#ifndef DEFINES_H_
#define DEFINES_H_

#if defined(__APPLE__)
#   ifndef MACOS
#       define MACOS
#   endif
#   if defined(__amd64__) || defined(__x86_64__)
#       ifndef MACOS64
#           define MACOS64
#       endif
#   else
#       ifndef MACOS32
#           define MACOS32
#       endif
#   endif
#elif defined(__linux__) || defined(__unix__)
#   ifndef LINUX
#       define LINUX
#   endif
#   if defined(__amd64__) || defined(__x86_64__)
#       ifndef LINUX64
#           define LINUX64
#       endif
#   else
#       ifndef LINUX32
#           define LINUX32
#       endif
#   endif
#elif defined(_WIN32)
#   ifndef WINDOWS
#       define WINDOWS 1
#   endif
#   if defined(_M_AMD64) || defined(_M_IA64)
#       ifndef WIN64
#           define WIN64
#       endif
#   else
#       ifndef WIN32
#           define WIN32
#       endif
#   endif
#else
#   warning "Can not determine system type, compilation may break."
#endif

#ifdef __GNUC__
#   ifndef IS_POSIX
#       define IS_POSIX 1
#   endif
#endif

#if defined(MACOS32) || defined(LINUX32) || defined(WIN32)
#   ifndef X86
#       define X86 1
#   endif
#endif

#if defined(MACOS64) || defined(LINUX64) || defined(WIN64)
#   ifndef X64
#       define X64 1
#   endif
#endif

#if defined(WINDOWS) && !defined(_WINRT_DLL)
    typedef __int8 int8_t;
    typedef unsigned __int8 uint8_t;
    typedef __int16 int16_t;
    typedef unsigned __int16 uint16_t;
    typedef __int32 int32_t;
    typedef unsigned __int32 uint32_t;
    typedef __int64 int64_t;
    typedef unsigned __int64 uint64_t;
#else
#   define __STDC_CONSTANT_MACROS 1
#   include <stdint.h>
#endif

#ifndef UINT64_C
#   define UINT64_C(c) c ## UL
#endif

#ifndef INT64_C
#   define INT64_C(c) c ## L
#endif

#if defined(WINDOWS)
#   define INLINE __inline
#else
#   define INLINE inline
#endif

#endif /* DEFINES_H_ */
