document.addEventListener("DOMContentLoaded", () => {
  const toggle = document.getElementById('profileToggle');
  const dropdown = document.getElementById('profileDropdown');

  if (!toggle || !dropdown) return;

  const openMenu = () => {
    dropdown.classList.add('show');
    toggle.setAttribute('aria-expanded', 'true');
    dropdown.setAttribute('aria-hidden', 'false');
  };

  const closeMenu = () => {
    dropdown.classList.remove('show');
    toggle.setAttribute('aria-expanded', 'false');
    dropdown.setAttribute('aria-hidden', 'true');
  };

  toggle.addEventListener('click', (e) => {
    e.stopPropagation();
    dropdown.classList.contains('show') ? closeMenu() : openMenu();
  });

  document.addEventListener('click', (e) => {
    if (!dropdown.contains(e.target) && !toggle.contains(e.target)) closeMenu();
  });

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeMenu();
  });
});
