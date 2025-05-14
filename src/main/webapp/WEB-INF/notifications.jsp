<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .notification-container {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1000;
    }

    .notification {
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        margin-bottom: 10px;
        padding: 16px;
        min-width: 300px;
        max-width: 400px;
        display: flex;
        align-items: center;
        gap: 12px;
        transform: translateX(120%);
        transition: transform 0.3s ease;
    }

    .notification.show {
        transform: translateX(0);
    }

    .notification-icon {
        width: 24px;
        height: 24px;
        flex-shrink: 0;
    }

    .notification-content {
        flex-grow: 1;
    }

    .notification-title {
        font-weight: 500;
        margin-bottom: 4px;
        color: #333;
    }

    .notification-message {
        color: #666;
        font-size: 0.9rem;
    }

    .notification-actions {
        display: flex;
        gap: 8px;
        margin-top: 12px;
    }

    .notification-btn {
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 0.9rem;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .notification-btn-primary {
        background-color: #2196F3;
        color: white;
        border: none;
    }

    .notification-btn-primary:hover {
        background-color: #1976D2;
    }

    .notification-btn-secondary {
        background-color: #f5f5f5;
        color: #333;
        border: 1px solid #ddd;
    }

    .notification-btn-secondary:hover {
        background-color: #e0e0e0;
    }

    .notification-btn-danger {
        background-color: #f44336;
        color: white;
        border: none;
    }

    .notification-btn-danger:hover {
        background-color: #d32f2f;
    }

    .notification-progress {
        position: absolute;
        bottom: 0;
        left: 0;
        height: 3px;
        background-color: #2196F3;
        width: 100%;
        transform-origin: left;
        animation: progress 5s linear forwards;
    }

    @keyframes progress {
        from { transform: scaleX(1); }
        to { transform: scaleX(0); }
    }

    /* Modal styles */
    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 2000;
        opacity: 0;
        visibility: hidden;
        transition: all 0.3s ease;
    }

    .modal-overlay.show {
        opacity: 1;
        visibility: visible;
    }

    .modal-container {
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
        padding: 24px;
        min-width: 400px;
        max-width: 500px;
        transform: scale(0.9);
        transition: transform 0.3s ease;
    }

    .modal-overlay.show .modal-container {
        transform: scale(1);
    }

    .modal-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;
    }

    .modal-title {
        font-size: 1.25rem;
        font-weight: 500;
        color: #333;
        margin: 0;
    }

    .modal-message {
        color: #666;
        margin-bottom: 24px;
        line-height: 1.5;
    }

    .modal-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
    }

    .modal-btn {
        padding: 8px 16px;
        border-radius: 4px;
        font-size: 0.9rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .modal-btn-primary {
        background-color: #2196F3;
        color: white;
        border: none;
    }

    .modal-btn-primary:hover {
        background-color: #1976D2;
    }

    .modal-btn-secondary {
        background-color: #f5f5f5;
        color: #333;
        border: 1px solid #ddd;
    }

    .modal-btn-secondary:hover {
        background-color: #e0e0e0;
    }

    .modal-btn-danger {
        background-color: #f44336;
        color: white;
        border: none;
    }

    .modal-btn-danger:hover {
        background-color: #d32f2f;
    }
</style>

<div class="notification-container" id="notificationContainer"></div>
<div class="modal-overlay" id="modalOverlay">
    <div class="modal-container">
        <div class="modal-header">
            <svg class="notification-icon" id="modalIcon" viewBox="0 0 24 24" fill="#ff9800">
                <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
            </svg>
            <h3 class="modal-title" id="modalTitle"></h3>
        </div>
        <div class="modal-message" id="modalMessage"></div>
        <div class="modal-actions" id="modalActions"></div>
    </div>
</div>

<script>
    class NotificationSystem {
        constructor() {
            this.container = document.getElementById('notificationContainer');
            this.modalOverlay = document.getElementById('modalOverlay');
            this.modalTitle = document.getElementById('modalTitle');
            this.modalMessage = document.getElementById('modalMessage');
            this.modalActions = document.getElementById('modalActions');
            this.modalIcon = document.getElementById('modalIcon');
        }

        show(options) {
            const {
                title,
                message,
                type = 'info',
                duration = 5000,
                actions = [],
                onClose = () => {}
            } = options;

            const notification = document.createElement('div');
            notification.className = 'notification';
            
            // Icon based on type
            let icon = '';
            switch(type) {
                case 'success':
                    icon = '<svg class="notification-icon" viewBox="0 0 24 24" fill="#4CAF50"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/></svg>';
                    break;
                case 'error':
                    icon = '<svg class="notification-icon" viewBox="0 0 24 24" fill="#f44336"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>';
                    break;
                case 'warning':
                    icon = '<svg class="notification-icon" viewBox="0 0 24 24" fill="#ff9800"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>';
                    break;
                default:
                    icon = '<svg class="notification-icon" viewBox="0 0 24 24" fill="#2196F3"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>';
            }

            let actionsHtml = '';
            if (actions.length > 0) {
                actionsHtml = '<div class="notification-actions">';
                actions.forEach(action => {
                    actionsHtml += '<button class="notification-btn notification-btn-' + (action.type || 'primary') + '" ' +
                                 'onclick="' + action.onClick + '">' +
                                 action.text +
                                 '</button>';
                });
                actionsHtml += '</div>';
            }

            notification.innerHTML = icon +
                '<div class="notification-content">' +
                '<div class="notification-title">' + title + '</div>' +
                '<div class="notification-message">' + message + '</div>' +
                actionsHtml +
                '</div>' +
                (duration ? '<div class="notification-progress"></div>' : '');

            this.container.appendChild(notification);
            
            // Trigger animation
            setTimeout(() => notification.classList.add('show'), 10);

            // Auto remove after duration
            if (duration) {
                setTimeout(() => {
                    this.remove(notification);
                    onClose();
                }, duration);
            }

            return notification;
        }

        remove(notification) {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 300);
        }

        confirm(options) {
            return new Promise((resolve) => {
                // Set modal content
                this.modalTitle.textContent = options.title;
                this.modalMessage.textContent = options.message;

                // Set icon based on type
                let iconPath = '';
                switch(options.type) {
                    case 'success':
                        iconPath = 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z';
                        this.modalIcon.setAttribute('fill', '#4CAF50');
                        break;
                    case 'error':
                        iconPath = 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z';
                        this.modalIcon.setAttribute('fill', '#f44336');
                        break;
                    case 'warning':
                        iconPath = 'M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z';
                        this.modalIcon.setAttribute('fill', '#ff9800');
                        break;
                    default:
                        iconPath = 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z';
                        this.modalIcon.setAttribute('fill', '#2196F3');
                }
                this.modalIcon.innerHTML = '<path d="' + iconPath + '"/>';

                // Set up buttons
                this.modalActions.innerHTML = 
                    '<button class="modal-btn modal-btn-' + (options.confirmType || 'primary') + '" ' +
                    'onclick="window.notificationSystem.hideModal(true)">' +
                    (options.confirmText || 'Confirm') +
                    '</button>' +
                    '<button class="modal-btn modal-btn-secondary" ' +
                    'onclick="window.notificationSystem.hideModal(false)">' +
                    (options.cancelText || 'Cancel') +
                    '</button>';

                // Show modal
                this.modalOverlay.classList.add('show');
                document.body.style.overflow = 'hidden';

                // Store resolve function
                this.modalResolve = resolve;
            });
        }

        hideModal(result) {
            this.modalOverlay.classList.remove('show');
            document.body.style.overflow = '';
            this.modalResolve(result);
        }
    }

    // Initialize notification system
    window.notificationSystem = new NotificationSystem();
</script> 