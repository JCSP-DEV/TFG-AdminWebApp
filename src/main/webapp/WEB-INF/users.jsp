<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Users Management</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <jsp:include page="notifications.jsp"/>
    <style>
        :root {
            --primary-color: #2196F3;
            --primary-dark: #1976D2;
            --error-color: #f44336;
            --success-color: #4CAF50;
            --warning-color: #ff9800;
            --text-color: #333;
            --border-radius: 8px;
            --box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f5f7fa;
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 2rem;
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid #eee;
        }

        .header h1 {
            color: var(--text-color);
            font-size: 1.8rem;
            font-weight: 500;
            margin: 0;
        }

        .header-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .btn {
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: var(--border-radius);
            font-size: 0.9rem;
            font-weight: 500;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .btn i {
            font-size: 1.1rem;
        }

        .btn-primary {
            background-color: var(--primary-color);
            color: white;
        }

        .btn-primary:hover {
            background-color: var(--primary-dark);
            transform: translateY(-1px);
        }

        .btn-danger {
            background-color: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background-color: #c82333;
            transform: translateY(-1px);
        }

        .error {
            background-color: #ffebee;
            color: var(--error-color);
            padding: 1rem;
            border-radius: var(--border-radius);
            margin-bottom: 1.5rem;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .error::before {
            content: "!";
            display: inline-block;
            width: 20px;
            height: 20px;
            background-color: var(--error-color);
            color: white;
            border-radius: 50%;
            text-align: center;
            line-height: 20px;
            font-weight: bold;
        }

        .table-container {
            overflow-x: auto;
            margin-top: 1rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            font-size: 0.9rem;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        th {
            background-color: #f8f9fa;
            font-weight: 500;
            color: #666;
            white-space: nowrap;
        }

        tr:hover {
            background-color: #f8f9fa;
        }

        .status-badge {
            padding: 0.4rem 0.8rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
            display: inline-block;
        }

        .status-verified {
            background-color: #e8f5e9;
            color: var(--success-color);
        }

        .status-unverified {
            background-color: #ffebee;
            color: var(--error-color);
        }

        .role-badge {
            padding: 0.4rem 0.8rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
            display: inline-block;
        }

        .role-admin {
            background-color: #e3f2fd;
            color: var(--primary-color);
        }

        .role-user {
            background-color: #f5f5f5;
            color: #666;
        }

        .date {
            color: #666;
            font-size: 0.9rem;
        }

        .actions {
            display: flex;
            gap: 0.5rem;
        }

        .action-btn {
            padding: 0.4rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s ease;
            background: none;
        }

        .action-btn:hover {
            background-color: #f5f5f5;
        }

        .action-btn svg {
            width: 18px;
            height: 18px;
            color: #666;
        }

        .search-bar {
            margin-bottom: 1.5rem;
            display: flex;
            gap: 1rem;
        }

        .search-input {
            flex: 1;
            padding: 0.8rem 1rem;
            border: 2px solid #eee;
            border-radius: var(--border-radius);
            font-size: 0.9rem;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            border-color: var(--primary-color);
            outline: none;
            box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
        }

        @media (max-width: 768px) {
            .container {
                padding: 1rem;
            }

            .header {
                flex-direction: column;
                gap: 1rem;
                align-items: flex-start;
            }

            .header-actions {
                width: 100%;
                justify-content: space-between;
            }

            th, td {
                padding: 0.8rem;
            }
        }

        .sortable {
            cursor: pointer;
            position: relative;
            padding-right: 1.5rem;
        }

        .sortable::after {
            content: "↕";
            position: absolute;
            right: 0.5rem;
            color: #999;
        }

        .sortable.asc::after {
            content: "↑";
            color: var(--primary-color);
        }

        .sortable.desc::after {
            content: "↓";
            color: var(--primary-color);
        }

        .sortable:hover {
            background-color: #f0f0f0;
        }

        .reset-sort {
            padding: 0.5rem 1rem;
            background-color: #f5f5f5;
            border: 1px solid #ddd;
            border-radius: var(--border-radius);
            color: #666;
            font-size: 0.9rem;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s ease;
        }

        .reset-sort:hover {
            background-color: #e0e0e0;
        }

        .reset-sort:active {
            transform: translateY(1px);
        }
    </style>
    <script>
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', function(e) {
            const searchText = e.target.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchText) ? '' : 'none';
            });
        });

        // Store original order of rows
        const tbody = document.querySelector('tbody');
        const originalRows = Array.from(tbody.querySelectorAll('tr'));

        // Reset sorting function
        function resetSorting() {
            // Remove sort classes from all headers
            document.querySelectorAll('th.sortable').forEach(th => {
                th.classList.remove('asc', 'desc');
            });
            
            // Restore original order
            originalRows.forEach(row => tbody.appendChild(row));
        }

        // Reset sort button click handler
        document.getElementById('resetSort').addEventListener('click', resetSorting);

        // Sorting functionality
        document.querySelectorAll('th.sortable').forEach(header => {
            let clickCount = 0;
            let clickTimer = null;

            header.addEventListener('click', () => {
                clickCount++;
                
                if (clickTimer === null) {
                    clickTimer = setTimeout(() => {
                        if (clickCount === 3) {
                            // Triple click - reset sorting
                            resetSorting();
                        } else {
                            // Single or double click - normal sorting
                            const sortKey = header.dataset.sort;
                            const currentDirection = header.classList.contains('asc') ? 'desc' : 'asc';
                            
                            // Remove sort classes from all headers
                            document.querySelectorAll('th.sortable').forEach(th => {
                                th.classList.remove('asc', 'desc');
                            });
                            
                            // Add sort class to clicked header
                            header.classList.add(currentDirection);
                            
                            // Get all rows and convert to array for sorting
                            const rows = Array.from(tbody.querySelectorAll('tr'));
                            
                            // Sort rows
                            rows.sort((a, b) => {
                                let aValue = a.querySelector(`td:nth-child(${Array.from(header.parentNode.children).indexOf(header) + 1})`).textContent.trim();
                                let bValue = b.querySelector(`td:nth-child(${Array.from(header.parentNode.children).indexOf(header) + 1})`).textContent.trim();
                                
                                // Handle special cases
                                if (sortKey === 'id') {
                                    aValue = parseInt(aValue);
                                    bValue = parseInt(bValue);
                                } else if (sortKey === 'verified') {
                                    aValue = aValue.toLowerCase().includes('verified');
                                    bValue = bValue.toLowerCase().includes('verified');
                                } else if (sortKey === 'createdDate' || sortKey === 'lastLoginDate') {
                                    aValue = new Date(aValue);
                                    bValue = new Date(bValue);
                                }
                                
                                if (currentDirection === 'asc') {
                                    return aValue > bValue ? 1 : -1;
                                } else {
                                    return aValue < bValue ? 1 : -1;
                                }
                            });
                            
                            // Reorder rows in the table
                            rows.forEach(row => tbody.appendChild(row));
                        }
                        
                        clickCount = 0;
                        clickTimer = null;
                    }, 300); // 300ms window for multiple clicks
                }
            });
        });

        // Delete user function
        function deleteUser(userId, username) {
            window.notificationSystem.confirm({
                title: 'Delete User',
                message: 'Are you sure you want to delete user "' + username + '"? This action cannot be undone.',
                type: 'warning',
                confirmText: 'Delete',
                confirmType: 'danger',
                cancelText: 'Cancel'
            }).then(confirmed => {
                if (confirmed) {
                    // Create form and submit it to trigger the Java endpoint
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/users/delete/' + userId;
                    document.body.appendChild(form);
                    form.submit();
                }
            });
        }
    </script>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Users Management</h1>
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary">
                    <i class="bi bi-person-plus"></i> Add User
                </a>
                <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0;">
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-box-arrow-right"></i> Logout
                    </button>
                </form>
            </div>
        </div>
        
        <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
        </c:if>

        <div class="search-bar">
            <input type="text" class="search-input" placeholder="Search users..." id="searchInput">
            <button class="reset-sort" id="resetSort" title="Reset sorting">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                    <path d="M3 3v5h5"></path>
                </svg>
                Reset Sort
            </button>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th class="sortable" data-sort="id">ID</th>
                        <th class="sortable" data-sort="username">Username</th>
                        <th class="sortable" data-sort="email">Email</th>
                        <th class="sortable" data-sort="role">Role</th>
                        <th class="sortable" data-sort="verified">Status</th>
                        <th class="sortable" data-sort="createdDate">Created Date</th>
                        <th class="sortable" data-sort="lastLoginDate">Last Login</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr data-user-id="${user.id}">
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.email}</td>
                            <td>
                                <span class="role-badge role-${user.role.toLowerCase()}">
                                    ${user.role}
                                </span>
                            </td>
                            <td>
                                <span class="status-badge status-${user.verified ? 'verified' : 'unverified'}">
                                    ${user.verified ? 'Verified' : 'Unverified'}
                                </span>
                            </td>
                            <td class="date">
                                ${user.createdDate}
                            </td>
                            <td class="date">
                                ${user.lastLoginDate}
                            </td>
                            <td>
                                <div class="actions">
                                    <button class="action-btn" title="Edit" onclick="window.location.href='${pageContext.request.contextPath}/users/edit/${user.id}'">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                        </svg>
                                    </button>
                                    <button class="action-btn" title="Delete" onclick="deleteUser('${user.id}', '${user.username}')">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <polyline points="3 6 5 6 21 6"></polyline>
                                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                        </svg>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html> 