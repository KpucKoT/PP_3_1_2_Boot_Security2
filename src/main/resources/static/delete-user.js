document.addEventListener('DOMContentLoaded', function () {
    const deleteForm = document.querySelector('#deleteUserForm');

    if (!deleteForm) {
        console.error('Форма удаления не найдена');
        return;
    }

    // Обработчик кнопки "Удалить"
    document.querySelectorAll('.delete-user-btn').forEach(button => {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            const userId = this.getAttribute('data-id');
            const username = this.getAttribute('data-username');
            const age = this.getAttribute('data-age');
            const roles = this.getAttribute('data-role');

            // Заполняем поля в модальном окне удаления
            fillDeleteModal(userId, username, age, roles);

            // Обновляем URL формы
            deleteForm.action = `/admin/${userId}/delete`;

            // Открываем модальное окно
            showDeleteModal();
        });
    });

    // Обработчик отправки формы удаления
    deleteForm.addEventListener('submit', function (event) {
        event.preventDefault();
        const formData = new FormData(deleteForm);

        fetch(deleteForm.action, {
            method: 'POST',
            body: formData,
            headers: {
                'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
            }
        })
            .then(response => {
                if (response.ok) {
                    alert('Пользователь успешно удален!');
                    location.reload(); // Перезагружаем страницу
                } else {
                    return response.json().then(data => {
                        throw new Error(data.message || 'Ошибка при удалении пользователя');
                    });
                }
            })
            .catch(error => alert('Ошибка: ' + error.message));
    });

    // Функция для заполнения модального окна
    function fillDeleteModal(userId, username, age, roles) {
        document.getElementById('viewUserId').value = userId;
        document.getElementById('viewUsername').value = username;
        document.getElementById('viewAge').value = age;
        document.getElementById('viewRoles').value = roles;
        document.getElementById('deleteUserId').value = userId;
    }

    // Функция для открытия модального окна
    function showDeleteModal() {
        const modal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
        modal.show();
    }

    // Обработчик кнопки "Отмена" в модальном окне удаления
    document.querySelector('#deleteUserModal .btn-secondary').addEventListener('click', function () {
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteUserModal'));
        if (modal) {
            modal.hide(); // Закрыть модальное окно
        }

        // Удаляем backdrop вручную
        removeBackdrop();
    });

    // Обработчик закрытия модального окна через крестик или клик вне модального окна
    document.getElementById('deleteUserModal').addEventListener('hidden.bs.modal', function () {
        removeBackdrop();
    });

    // Функция для удаления backdrop и восстановления прокрутки
    function removeBackdrop() {
        const backdrop = document.querySelector('.modal-backdrop');
        if (backdrop) {
            backdrop.remove();
        }

        // Восстанавливаем прокрутку страницы
        document.body.style.overflow = 'auto';
        document.body.style.paddingRight = '0';
    }
});