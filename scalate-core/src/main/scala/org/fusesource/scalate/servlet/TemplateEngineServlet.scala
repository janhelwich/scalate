/**
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.scalate.servlet

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.fusesource.scalate.util.Logging
import org.fusesource.scalate.TemplateEngine

object TemplateEngineServlet {
  protected var singleton: TemplateEngineServlet = _

  def apply(): TemplateEngineServlet = singleton

  def update(servlet: TemplateEngineServlet): Unit = singleton = servlet
}

/**
 * Servlet which renders the requested Scalate template.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class TemplateEngineServlet extends HttpServlet {

  var templateEngine: ServletTemplateEngine = _

  override def init(config: ServletConfig) {
    super.init(config)

    templateEngine = new ServletTemplateEngine(config)

    // register the template engine and servlet so they can be easily resolved
    TemplateEngineServlet() = this
    ServletTemplateEngine(getServletContext) = templateEngine
  }

  
  override def service(request: HttpServletRequest, response: HttpServletResponse) {
    render(request.getServletPath, request, response)
  }

  def render(template: String, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val context = new ServletRenderContext(templateEngine, request, response, getServletContext)
    context.layout(template)
  }

}
