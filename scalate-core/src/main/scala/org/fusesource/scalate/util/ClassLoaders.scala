package org.fusesource.scalate.util

import java.io.File
import java.net.{URI, URLClassLoader}

/**
 * @version $Revision : 1.1 $
 */

object ClassLoaders extends Logging {
  def classLoaderList[T](aClass: Class[T]): List[String] = {
    classLoaderList(aClass.getClassLoader)
  }

  type AntLikeClassLoader = {
    def getClasspath: String
  }
  
  object AntLikeClassLoader {
    def unapply(ref: AnyRef): Option[AntLikeClassLoader] = {
      try {
        val method = ref.getClass.getMethod("getClasspath")
        if (method.getReturnType == classOf[String])
          Some(ref.asInstanceOf[AntLikeClassLoader])
        else
          None
      } catch {
        case e: NoSuchMethodException => None
      }
    }
  }

  def classLoaderList[T](classLoader: ClassLoader): List[String] = {
    classLoader match {
      case cl: URLClassLoader =>
        cl.getURLs.toList.map {
          // on windows the path can include %20
          // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4466485
          // so lets use URI as a workaround
          u =>
            val uri = new URI(u.toString)
            new File(uri.getPath).getCanonicalPath
/*
            val n = new File(uri.getPath).getCanonicalPath
            if (n.contains(' ')) {"\"" + n + "\""} else {n}
*/
        }

      case AntLikeClassLoader(acp) =>
        val cp = acp.getClasspath
        cp.split(File.pathSeparator).toList

      case _ =>
        warning("Cannot introspect on class loader: " + classLoader + " of type " + classLoader.getClass.getCanonicalName)
        val parent = classLoader.getParent
        if (parent != null && parent != classLoader) {
          classLoaderList(parent)
        }
        else {
          Nil
        }
    }

  }


}