/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.restygwt.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.HasAnnotations;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import jakarta.ws.rs.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.fusesource.restygwt.rebind.BaseSourceCreator.ERROR;
import static org.fusesource.restygwt.rebind.util.AnnotationUtils.getAnnotation;

/**
 * @author sj
 */
public class RestAnnotationValueProvider {

  private static final boolean JAX_RS_AVAILABLE;

  static {
    boolean jaxRsAvailable = false;
    try {
      Class.forName("jakarta.ws.rs.Path");
      jaxRsAvailable = true;
    } catch (ClassNotFoundException ex) {
      // do nothing
    }
    JAX_RS_AVAILABLE = jaxRsAvailable;
  }

  public static String getRestMethod(JMethod method, TreeLogger logger) throws UnableToCompleteException {
    String restMethod = null;

    if (JAX_RS_AVAILABLE) {
      final Annotation[] annotations = method.getAnnotations();
      for (Annotation annotation : annotations) {
        HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
        // Check is HttpMethod
        if (httpMethod != null) {
          if (restMethod != null) {
            // Error, see description of HttpMethod
            logger.log(ERROR, "Invalid method. It is an error for a method to be annotated with more than one annotation that is annotated with HttpMethod: " + method.getReadableDeclaration());
            throw new UnableToCompleteException();
          }
          restMethod = httpMethod.value();
        }
      }
    }

    return restMethod;
  }

  public static String getPathValue(HasAnnotations annotatedType, TreeLogger logger) throws UnableToCompleteException {
    String path = null;
    String[] pathValues = null;

    if (JAX_RS_AVAILABLE) {
      Path pathAnnotation = getAnnotation(annotatedType, Path.class);
      if (pathAnnotation != null) {
        path = pathAnnotation.value();
      }
    }
    return path;
  }

  public static boolean isPathValue(Annotation annotation, Method method) {
      return annotation.annotationType().getName().equals("jakarta.ws.rs.Path");
  }

  public static String getPathValue(HasAnnotations annotatedType) {
    try {
      return getPathValue(annotatedType, TreeLogger.NULL);
    } catch (UnableToCompleteException ex) {
      return null;
    }
  }

  public static String getParamPathValue(JParameter arg) {
    String paramPathValue = null;

    if (JAX_RS_AVAILABLE) {
      PathParam paramPath = getAnnotation(arg, PathParam.class);
      if (paramPath != null) {
        paramPathValue = paramPath.value();
      }
    }
    return paramPathValue;
  }

  public static String getHeaderParamValue(JParameter arg) {
    String headerParamValue = null;

    if (JAX_RS_AVAILABLE) {
      HeaderParam headerParam = getAnnotation(arg, HeaderParam.class);
      if (headerParam != null) {
        headerParamValue = headerParam.value();
      }
    }

    return headerParamValue;
  }

  public static String getFormParamValue(JParameter arg) {
    String formParamValue = null;

    if (JAX_RS_AVAILABLE) {
      FormParam formParam = getAnnotation(arg, FormParam.class);
      if (formParam != null) {
        formParamValue = formParam.value();
      }
    }

    return formParamValue;
  }

  public static String getQueryParamValue(JParameter arg) {
    String queryParamValue = null;

    if (JAX_RS_AVAILABLE) {
      QueryParam queryParam = getAnnotation(arg, QueryParam.class);
      if (queryParam != null) {
        queryParamValue = queryParam.value();
      }
    }

    return queryParamValue;
  }

  public static String getProducesFirstValue(JMethod method) {
    String producesValue = null;

    if (JAX_RS_AVAILABLE) {
      Produces producesAnnotation = getAnnotation(method, Produces.class);
      if (producesAnnotation != null) {
        producesValue = producesAnnotation.value()[0];
      } else {
        producesAnnotation = getAnnotation(method.getEnclosingType(), Produces.class);
        if (producesAnnotation != null) {
          producesValue = producesAnnotation.value()[0];
        }
      }
    }

    return producesValue;
  }

  public static String getConsumesFirstValue(JMethod method) {
    String consumesValue = null;

    if (JAX_RS_AVAILABLE) {
      Consumes consumesAnnotation = getAnnotation(method, Consumes.class);
      if (consumesAnnotation != null) {
        consumesValue = consumesAnnotation.value()[0];
      } else {
        consumesAnnotation = getAnnotation(method.getEnclosingType(), Consumes.class);
        if (consumesAnnotation != null) {
          consumesValue = consumesAnnotation.value()[0];
        }
      }
    }
    return consumesValue;
  }
}
