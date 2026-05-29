(function () {
  function openImageModal(src, title = 'Фото') {
    let modal = document.getElementById('image-modal');

    if (!modal) {
      modal = document.createElement('div');
      modal.id = 'image-modal';
      modal.className = 'image-modal';
      modal.innerHTML = `
        <div class="image-modal__content">
          <div class="image-modal__top">
            <div class="image-modal__title"></div>
            <button class="image-modal__close" type="button">✕</button>
          </div>
          <div class="image-modal__body">
            <img alt="">
          </div>
        </div>
      `;
      document.body.appendChild(modal);

      modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.classList.remove('is-open');
      });

      modal.querySelector('.image-modal__close').addEventListener('click', () => {
        modal.classList.remove('is-open');
      });
    }

    modal.querySelector('.image-modal__title').textContent = title;
    const img = modal.querySelector('img');
    img.src = src;
    img.alt = title;
    modal.classList.add('is-open');
  }

  function syncInputFiles(input, files) {
    const dt = new DataTransfer();
    files.forEach((file) => dt.items.add(file.raw));
    input.files = dt.files;
  }

  function renderPreview(container, files, input) {
    if (!container) return;

    if (!files.length) {
      container.innerHTML = '<div class="empty-state">Файлы не выбраны</div>';
      return;
    }

    container.innerHTML = `
      <div class="upload-gallery">
        ${files.map((file, index) => `
          <article class="upload-item">
            <img class="upload-item__img" src="${file.url}" alt="Фото ${index + 1}">
            <div class="upload-item__bar">
              <div class="upload-item__name">Фото ${index + 1}</div>
              <div class="upload-item__actions">
                <button class="upload-item__btn" type="button" data-image-view>👁</button>
                <button class="upload-item__btn upload-item__remove" type="button" data-remove-selected>🗑</button>
              </div>
            </div>
          </article>
        `).join('')}
      </div>
    `;

    container.querySelectorAll('[data-image-view]').forEach((btn, index) => {
      btn.addEventListener('click', () => {
        openImageModal(files[index].url, `Фото ${index + 1}`);
      });
    });

    container.querySelectorAll('[data-remove-selected]').forEach((btn, index) => {
      btn.addEventListener('click', () => {
        files.splice(index, 1);
        syncInputFiles(input, files);
        renderPreview(container, files, input);
      });
    });
  }

  function enhanceFileInputs() {
    document.querySelectorAll('input.js-file-input[type="file"]:not([data-enhanced])').forEach((input) => {
      input.dataset.enhanced = '1';

      if (!input.id) {
        input.id = 'file-' + Math.random().toString(36).slice(2, 9);
      }

      const widget = input.closest('[data-upload-widget]') || input.closest('.upload-section') || input.closest('.panel') || input.parentElement;
      const previewContainer = widget ? widget.querySelector('.js-file-preview') : null;
      const countBadge = widget ? widget.querySelector('.js-upload-count') : null;

      const container = document.createElement('div');
      container.className = 'file-control';

      const row = document.createElement('div');
      row.className = 'file-control__row';

      const icon = document.createElement('div');
      icon.className = 'file-control__icon';
      icon.textContent = input.multiple ? '🖼' : '📎';

      const meta = document.createElement('div');
      meta.className = 'file-control__meta';

      const title = document.createElement('div');
      title.className = 'file-control__title';
      title.textContent = input.multiple ? 'Загрузка изображений' : 'Загрузка файла';

      const status = document.createElement('div');
      status.className = 'file-control__status';
      status.textContent = 'Файлы не выбраны';

      meta.appendChild(title);
      meta.appendChild(status);

      const count = document.createElement('span');
      count.className = 'file-control__count';
      count.textContent = '0';

      const label = document.createElement('label');
      label.className = 'file-control__button';
      label.setAttribute('for', input.id);
      label.textContent = input.multiple ? 'Выбрать файлы' : 'Выбрать файл';

      row.appendChild(icon);
      row.appendChild(meta);
      row.appendChild(count);
      row.appendChild(label);

      const parent = input.parentNode;
      parent.insertBefore(container, input);
      container.appendChild(row);
      container.appendChild(input);

      input.classList.add('file-control__input');

      let selectedFiles = [];

      const updateState = () => {
        const filesCount = selectedFiles.length;
        count.textContent = String(filesCount);

        if (filesCount === 0) {
          status.textContent = 'Файлы не выбраны';
          count.classList.remove('is-selected');
        } else {
          status.textContent = `Выбрано ${filesCount} файл${filesCount === 1 ? '' : 'ов'}`;
          count.classList.add('is-selected');
        }

        if (countBadge) {
          countBadge.textContent = `${filesCount}/5`;
        }

        renderPreview(previewContainer, selectedFiles.map((f) => ({
          raw: f.raw,
          url: f.url
        })), input);
      };

      input.addEventListener('change', () => {
        selectedFiles = [...input.files].map((file) => ({
          raw: file,
          url: URL.createObjectURL(file)
        }));
        updateState();
      });

      updateState();
    });
  }

  function enhanceSpecRows() {
    const list = document.getElementById('component-spec-list');
    const template = document.getElementById('component-spec-template');
    const addBtn = document.getElementById('component-spec-add');

    if (!list || !template || !addBtn) return;

    const reindex = () => {
      const rows = list.querySelectorAll('.js-spec-row');
      rows.forEach((row, index) => {
        const select = row.querySelector('select');
        const input = row.querySelector('input');
        if (select) select.name = `specifications[${index}].id`;
        if (input) input.name = `specifications[${index}].value`;
      });
    };

    const bindRemoveButtons = () => {
      list.querySelectorAll('.js-remove-spec').forEach((btn) => {
        btn.onclick = () => {
          const row = btn.closest('.js-spec-row');
          if (row) row.remove();
          reindex();
        };
      });
    };

    addBtn.addEventListener('click', () => {
      const index = list.querySelectorAll('.js-spec-row').length;
      const html = template.innerHTML.replaceAll('__INDEX__', String(index));
      const wrapper = document.createElement('div');
      wrapper.innerHTML = html.trim();
      const row = wrapper.firstElementChild;
      list.appendChild(row);
      reindex();
      bindRemoveButtons();
    });

    bindRemoveButtons();
  }

  function enhanceImageViews() {
    document.querySelectorAll('[data-image-src]').forEach((item) => {
      item.style.cursor = 'zoom-in';
      item.addEventListener('click', () => {
        openImageModal(item.dataset.imageSrc, item.dataset.imageTitle || 'Фото');
      });
    });

    document.querySelectorAll('[data-image-view]').forEach((btn) => {
      const item = btn.closest('[data-image-src]');
      if (!item) return;

      btn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        openImageModal(item.dataset.imageSrc, item.dataset.imageTitle || 'Фото');
      });
    });
  }

  document.addEventListener('DOMContentLoaded', () => {
    enhanceFileInputs();
    enhanceSpecRows();
    enhanceImageViews();
  });
})();

(function () {
  function formatPrice(value) {
    const num = Number(value || 0);
    return new Intl.NumberFormat('ru-RU').format(num) + ' BYN';
  }

  function enhanceComponentLivePreview() {
    const nameInput = document.getElementById('component-name');
    const typeSelect = document.getElementById('component-type');
    const producerSelect = document.getElementById('component-producer');
    const priceInput = document.getElementById('component-price');
    const stockInput = document.getElementById('component-stock');

    const previewImg = document.getElementById('component-preview-img');
    const previewName = document.getElementById('component-preview-name');
    const previewType = document.getElementById('component-preview-type');
    const previewProducer = document.getElementById('component-preview-producer');
    const previewPrice = document.getElementById('component-preview-price');
    const previewStock = document.getElementById('component-preview-stock');

    if (!nameInput || !typeSelect || !producerSelect || !priceInput || !stockInput) return;
    if (!previewImg || !previewName || !previewType || !previewProducer || !previewPrice || !previewStock) return;

    function updatePreview() {
      const selectedTypeText = typeSelect.options[typeSelect.selectedIndex]?.textContent?.trim() || 'Тип';
      const selectedProducerText = producerSelect.options[producerSelect.selectedIndex]?.textContent?.trim() || 'Производитель';

      previewName.textContent = nameInput.value.trim() || 'Новый компонент';
      previewType.textContent = selectedTypeText;
      previewProducer.textContent = selectedProducerText;
      previewPrice.textContent = formatPrice(priceInput.value);
      previewStock.textContent = stockInput.value || '0';
    }

    nameInput.addEventListener('input', updatePreview);
    typeSelect.addEventListener('change', updatePreview);
    producerSelect.addEventListener('change', updatePreview);
    priceInput.addEventListener('input', updatePreview);
    stockInput.addEventListener('input', updatePreview);

    updatePreview();
  }

  document.addEventListener('DOMContentLoaded', enhanceComponentLivePreview);
})();

(function () {
  function formatMoney(value) {
    const num = Number(value || 0);
    return new Intl.NumberFormat('ru-RU').format(num) + ' BYN';
  }

  function enhanceBuildLivePreview() {
    const form = document.querySelector('[data-build-form]');
    if (!form) return;

    const nameInput = document.getElementById('build-name');
    const previewName = document.getElementById('build-preview-name');
    const previewTotal = document.getElementById('build-preview-total');
    const previewTotalText = document.getElementById('build-preview-total-text');
    const previewFilled = document.getElementById('build-preview-filled');
    const previewFilledText = document.getElementById('build-preview-filled-text');

    const slotCards = [...form.querySelectorAll('.js-build-slot')];

    function updateSlot(card) {
      const qtyInput = card.querySelector('.js-slot-qty');
      const qtyLabel = card.querySelector('.js-slot-qty-label');
      const subtotalEl = card.querySelector('.js-slot-subtotal');

      const hasComponent = card.dataset.hasComponent === 'true';
      const fixed = card.dataset.fixed === 'true';

      let qty = Number(qtyInput?.value || card.dataset.quantity || 1);
      if (fixed) qty = 1;

      if (qtyInput) qtyInput.value = qty;
      if (qtyLabel) qtyLabel.textContent = String(qty);

      const price = Number(card.dataset.componentPrice || 0);
      const subtotal = hasComponent ? price * qty : 0;

      if (subtotalEl) subtotalEl.textContent = formatMoney(subtotal);

      return { hasComponent, subtotal };
    }

    function updatePreview() {
      if (previewName) previewName.textContent = nameInput?.value?.trim() || 'Новая сборка';

      let total = 0;
      let filled = 0;

      slotCards.forEach((card) => {
        const result = updateSlot(card);
        total += result.subtotal;
        if (result.hasComponent) filled++;
      });

      const slotsTotal = `${filled}/${slotCards.length}`;

      if (previewTotal) previewTotal.textContent = formatMoney(total);
      if (previewTotalText) previewTotalText.textContent = formatMoney(total);

      if (previewFilled) previewFilled.textContent = slotsTotal;
      if (previewFilledText) previewFilledText.textContent = slotsTotal;
    }

    if (nameInput) {
      nameInput.addEventListener('input', updatePreview);
    }

    slotCards.forEach((card) => {
      const qtyInput = card.querySelector('.js-slot-qty');
      if (qtyInput) {
        qtyInput.addEventListener('input', updatePreview);
        qtyInput.addEventListener('change', updatePreview);
      }
    });

    updatePreview();
  }

  document.addEventListener('DOMContentLoaded', enhanceBuildLivePreview);
})();