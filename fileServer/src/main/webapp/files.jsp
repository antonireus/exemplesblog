<%@ page info="File Form & List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fitxers</title>
</head>
<body>
<h1>Fitxers</h1>

<form action="<c:url value="/file" />" method="post" enctype="multipart/form-data">

    <label for="file">File</label>
    <input id="file" type="file" name="file"/><br />

    <input type="submit" name="submit" value="Enviar" />
</form>

<p>File list</p>

<c:choose>
    <c:when test="${fn:length(files) > 0}">
        <ul>
            <c:forEach items="${files}" var="file">
                <li>
                    <c:url var="downloadLink" value="/file">
                        <c:param name="download" value="${file.name}" />
                    </c:url>
                    <a href="${downloadLink}"><c:out value="${file.name}" /></a><br />
                    <c:out value="${file.absolutePath}" /><br />
                    <c:out value="${file.length()}" /><br />
                </li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <p>Empty!</p>
    </c:otherwise>
</c:choose>
</body>
</html>