/**
 *  Java Enterprise BenchmarkImpl Tool
 *  Copyright (C) 2017  Alexander Nilov arifolth@gmail.com 
 */


/**
 * 
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package ru.arifolth;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.arifolth.ws.ServiceFault;
import ru.arifolth.ws.ServiceRequest;
import ru.arifolth.ws.ServiceResponse;
import ru.arifolth.ws.WebServiceInterface;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * Created by ANilov on 14.02.2017.
 */
@Stateless(name = "WebService", mappedName = "ejb/WebService")
@Remote(value = ru.arifolth.ws.WebServiceInterface.class)
@WebService(
        endpointInterface = "ru.arifolth.ws.WebServiceInterface",
        targetNamespace = "http://www.arifolth.ru/webService",
        serviceName = "WebService",
        portName = "WebServicePort",
        wsdlLocation = "wsdl/WebService.wsdl"
)
@SchemaValidation
@Component
public class WebServiceImpl implements WebServiceInterface {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebServiceImpl.class);

    @Override
    public ServiceResponse getData(ServiceRequest request) throws ServiceFault {
        LOGGER.debug("Sending WS Response");
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setResponseBody(RandomStringUtils.randomAlphabetic(255).getBytes());
        return serviceResponse;
    }
}
