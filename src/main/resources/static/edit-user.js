document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#editUserForm');

    // Обработчик кнопки "Редактировать"
    document.querySelectorAll('.edit-user-btn').forEach(button => {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            const userId = this.getAttribute('data-id'); // Получаем ID пользователя

            fetch(`/admin/${userId}`)
                .then(response => response.json())
                .then(user => {
                    // Заполняем форму данными пользователя
                    document.getElementById('userId').value = user.id; // Устанавливаем ID
                    document.getElementById('username').value = user.username;
                    document.getElementById('age').value = user.age;
                    document.getElementById('role').value = user.roles[0].id;

                    // Обновляем URL формы
                    form.action = `/admin/edit/${userId}`; // Устанавливаем правильный URL

                    // Открываем модальное окно
                    const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
                    modal.show();
                })
                .catch(error => console.error('Ошибка:', error));
        });
    });

    // Обработчик отправки формы
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        const formData = new FormData(form);

        fetch(form.action, {
            method: 'POST',
            body: formData,
            headers: {
                'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
            }
        })
            .then(response => {
                if (response.ok) {
                    alert('Данные успешно сохранены!');
                    location.reload();
                } else {
                    throw new Error('Ошибка при сохранении данных');
                }
            })
            .catch(error => alert('Ошибка: ' + error.message));
    });

    // Обработчик кнопки "Отмена"
    document.querySelector('#editUserModal .btn-secondary').addEventListener('click', function () {
        const modal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
        if (modal) {
            modal.hide(); // Закрыть модальное окно
        }

        // Удаляем backdrop вручную
        removeBackdrop();
    });

    // Обработчик закрытия модального окна через крестик или клик вне модального окна
    document.getElementById('editUserModal').addEventListener('hidden.bs.modal', function () {
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








