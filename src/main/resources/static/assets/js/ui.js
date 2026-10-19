(function () {
  // Просмотрщик на весь экран. Для ленты фото умеет листать: стрелки, клавиши ← →, Esc закрывает.
  const viewer = {
    modal: null,
    items: [],
    index: 0,
    title: 'Фото',
    onChange: null,
    returnFocus: null
  };

  function ensureModal() {
    if (viewer.modal) return viewer.modal;

    const modal = document.createElement('div');
    modal.id = 'image-modal';
    modal.className = 'image-modal';
    modal.setAttribute('role', 'dialog');
    modal.setAttribute('aria-modal', 'true');
    modal.innerHTML = `
      <div class="image-modal__content">
        <div class="image-modal__top">
          <div class="image-modal__title"></div>
          <div class="image-modal__counter" aria-live="polite"></div>
          <button class="image-modal__close" type="button" aria-label="Закрыть">✕</button>
        </div>
        <div class="image-modal__body">
          <img alt="">
          <button class="carousel__nav carousel__nav--prev" type="button" data-viewer-prev aria-label="Предыдущее фото">‹</button>
          <button class="carousel__nav carousel__nav--next" type="button" data-viewer-next aria-label="Следующее фото">›</button>
        </div>
      </div>
    `;
    document.body.appendChild(modal);

    modal.addEventListener('click', (e) => {
      if (e.target === modal) closeViewer();
    });
    modal.querySelector('.image-modal__close').addEventListener('click', closeViewer);
    modal.querySelector('[data-viewer-prev]').addEventListener('click', () => showInViewer(viewer.index - 1));
    modal.querySelector('[data-viewer-next]').addEventListener('click', () => showInViewer(viewer.index + 1));

    document.addEventListener('keydown', (e) => {
      if (!modal.classList.contains('is-open')) return;
      if (e.key === 'Escape') closeViewer();
      if (e.key === 'ArrowLeft') showInViewer(viewer.index - 1);
      if (e.key === 'ArrowRight') showInViewer(viewer.index + 1);
    });

    // свайп по фото в просмотрщике
    let touchX = null;
    const body = modal.querySelector('.image-modal__body');
    body.addEventListener('touchstart', (e) => { touchX = e.touches[0].clientX; }, { passive: true });
    body.addEventListener('touchend', (e) => {
      if (touchX === null) return;
      const dx = e.changedTouches[0].clientX - touchX;
      touchX = null;
      if (Math.abs(dx) > 40) showInViewer(viewer.index + (dx < 0 ? 1 : -1));
    });

    viewer.modal = modal;
    return modal;
  }

  function showInViewer(index) {
    if (index < 0 || index >= viewer.items.length) return;
    viewer.index = index;

    const modal = viewer.modal;
    const many = viewer.items.length > 1;
    const img = modal.querySelector('img');
    img.src = viewer.items[index];
    img.alt = many ? `${viewer.title}, фото ${index + 1}` : viewer.title;
    modal.querySelector('.image-modal__title').textContent = viewer.title;
    modal.querySelector('.image-modal__counter').textContent = many ? `${index + 1} / ${viewer.items.length}` : '';

    const prev = modal.querySelector('[data-viewer-prev]');
    const next = modal.querySelector('[data-viewer-next]');
    prev.hidden = !many;
    next.hidden = !many;
    prev.disabled = index === 0;
    next.disabled = index === viewer.items.length - 1;

    if (viewer.onChange) viewer.onChange(index);
  }

  function openImageGallery(items, index = 0, title = 'Фото', onChange = null) {
    ensureModal();
    viewer.items = items;
    viewer.title = title;
    viewer.onChange = onChange;
    viewer.returnFocus = document.activeElement;
    viewer.modal.classList.add('is-open');
    showInViewer(index);
    viewer.modal.querySelector('.image-modal__close').focus();
  }

  function closeViewer() {
    if (!viewer.modal) return;
    viewer.modal.classList.remove('is-open');
    if (viewer.returnFocus) viewer.returnFocus.focus();
  }

  function openImageModal(src, title = 'Фото') {
    openImageGallery([src], 0, title);
  }

  function enhanceCarousels() {
    document.querySelectorAll('[data-carousel]').forEach((carousel) => {
      const track = carousel.querySelector('.carousel__track');
      const slides = [...carousel.querySelectorAll('.carousel__slide')];
      const thumbs = [...carousel.querySelectorAll('[data-carousel-to]')];
      const current = carousel.querySelector('[data-carousel-current]');
      const prev = carousel.querySelector('[data-carousel-prev]');
      const next = carousel.querySelector('[data-carousel-next]');
      const title = carousel.dataset.title || 'Фото';
      const sources = slides.map((slide) => slide.dataset.src);
      let index = 0;

      const setActive = (i) => {
        index = i;
        if (current) current.textContent = String(i + 1);
        if (prev) prev.disabled = i === 0;
        if (next) next.disabled = i === slides.length - 1;
        thumbs.forEach((thumb, t) => {
          thumb.classList.toggle('is-active', t === i);
          if (t === i) thumb.scrollIntoView({ block: 'nearest', inline: 'nearest' });
        });
      };

      const goTo = (i) => {
        const target = Math.max(0, Math.min(slides.length - 1, i));
        track.scrollTo({ left: target * track.clientWidth });
        setActive(target);
      };

      // текущий слайд определяем по прокрутке — так работают и свайп, и тачпад
      let scrollTimer = null;
      track.addEventListener('scroll', () => {
        clearTimeout(scrollTimer);
        scrollTimer = setTimeout(() => {
          const i = Math.round(track.scrollLeft / track.clientWidth);
          if (i !== index) setActive(i);
        }, 60);
      }, { passive: true });

      if (prev) prev.addEventListener('click', () => goTo(index - 1));
      if (next) next.addEventListener('click', () => goTo(index + 1));
      thumbs.forEach((thumb) => {
        thumb.addEventListener('click', () => goTo(Number(thumb.dataset.carouselTo)));
      });

      track.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') { e.preventDefault(); goTo(index - 1); }
        if (e.key === 'ArrowRight') { e.preventDefault(); goTo(index + 1); }
        if (e.key === 'Enter') openImageGallery(sources, index, title, goTo);
      });

      const openViewer = () => openImageGallery(sources, index, title, goTo);
      slides.forEach((slide) => slide.addEventListener('click', openViewer));
      carousel.querySelector('[data-carousel-zoom]')?.addEventListener('click', openViewer);

      // после поворота экрана / ресайза остаёмся на том же фото
      window.addEventListener('resize', () => {
        track.style.scrollBehavior = 'auto';
        track.scrollLeft = index * track.clientWidth;
        track.style.scrollBehavior = '';
      });

      setActive(0);
    });
  }

  function syncInputFiles(input, files) {
    const dt = new DataTransfer();
    files.forEach((file) => dt.items.add(file.raw));
    input.files = dt.files;
  }

  function renderPreview(container, files, input, onChange) {
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
        if (onChange) {
          onChange();
        } else {
          renderPreview(container, files, input);
        }
      });
    });
  }

  function pluralFiles(n) {
    const mod10 = n % 10;
    const mod100 = n % 100;
    if (mod10 === 1 && mod100 !== 11) return 'файл';
    if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return 'файла';
    return 'файлов';
  }

  function formatMb(bytes) {
    return (bytes / 1024 / 1024).toFixed(1).replace('.', ',');
  }

  /**
   * Общий размер всех файлов формы не должен превышать лимит запроса — иначе сервер отклонит её целиком.
   */
  function guardUploadForms() {
    document.querySelectorAll('form[enctype="multipart/form-data"]').forEach((form) => {
      const widget = form.querySelector('[data-upload-widget][data-max-request-bytes]');
      if (!widget) return;

      form.addEventListener('submit', (e) => {
        const maxRequestBytes = Number(widget.dataset.maxRequestBytes);
        const inputs = [...form.querySelectorAll('input[type="file"]')];
        const total = inputs.flatMap((input) => [...input.files]).reduce((sum, file) => sum + file.size, 0);
        if (total <= maxRequestBytes) return;

        e.preventDefault();
        const message = `Все фото вместе весят ${formatMb(total)} МБ, а можно не больше ${widget.dataset.maxRequestMb} МБ. Убери часть фото.`;
        inputs.forEach((input) => input.dispatchEvent(new CustomEvent('upload-error', { detail: message })));
        widget.scrollIntoView({ behavior: 'smooth', block: 'center' });
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
      let truncated = false;
      let errorText = '';

      const maxImages = Number(widget?.dataset.max || 5);
      const maxFileBytes = Number(widget?.dataset.maxFileBytes || 0);
      const maxFileMb = widget?.dataset.maxFileMb;
      const keptCount = () => widget
        ? widget.querySelectorAll('input[name="keptImageIds"]:checked').length
        : 0;
      // одиночный выбор (фото производителя) заменяет текущее фото, поэтому лимит не зависит от уже загруженных
      const allowedNew = () => input.multiple ? Math.max(maxImages - keptCount(), 0) : 1;

      const updateState = () => {
        const filesCount = selectedFiles.length;
        count.textContent = String(filesCount);

        if (filesCount === 0) {
          status.textContent = allowedNew() === 0 ? `Достигнут лимит ${maxImages} фото` : 'Файлы не выбраны';
          count.classList.remove('is-selected');
        } else {
          status.textContent = `Выбрано ${filesCount} ${pluralFiles(filesCount)}`
            + (truncated ? ` (лимит ${maxImages} фото)` : '');
          count.classList.add('is-selected');
        }

        if (errorText) {
          status.textContent = errorText + (filesCount ? ` Выбрано: ${filesCount}.` : '');
        }
        status.classList.toggle('is-error', Boolean(errorText));

        if (countBadge) {
          const total = input.multiple ? keptCount() + filesCount : (filesCount || keptCount());
          countBadge.textContent = `${total}/${maxImages}`;
        }

        // после удаления файла из превью старое сообщение об ошибке уже неактуально
        renderPreview(previewContainer, selectedFiles, input, () => {
          errorText = '';
          updateState();
        });
      };

      input.addEventListener('change', () => {
        const all = [...input.files];
        // слишком большие файлы сервер всё равно отклонит — отсекаем сразу и говорим какие
        const tooBig = maxFileBytes ? all.filter((file) => file.size > maxFileBytes) : [];
        const files = all.filter((file) => !tooBig.includes(file));

        errorText = tooBig.length
          ? `Не добавлены — больше ${maxFileMb} МБ: ${tooBig.map((f) => `${f.name} (${formatMb(f.size)} МБ)`).join(', ')}.`
          : '';
        truncated = files.length > allowedNew();
        selectedFiles = files.slice(0, allowedNew()).map((file) => ({
          raw: file,
          url: URL.createObjectURL(file)
        }));
        if (truncated || tooBig.length) {
          syncInputFiles(input, selectedFiles);
        }
        updateState();
      });

      input.addEventListener('upload-error', (e) => {
        errorText = e.detail;
        updateState();
      });

      if (widget) {
        widget.querySelectorAll('input[name="keptImageIds"]').forEach((checkbox) => {
          checkbox.addEventListener('change', updateState);
        });
      }

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
        if (select) select.name = `specifications[${index}].specificationTypeId`;
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
      item.addEventListener('click', (e) => {
        // клик по галочке «Оставить» не должен открывать просмотр
        if (e.target.closest('input, label')) return;
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
    guardUploadForms();
    enhanceSpecRows();
    enhanceImageViews();
    enhanceCarousels();
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