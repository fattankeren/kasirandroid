# Implementation Plan - Modern POS UI (3 Main Pages)

Transforming the application into a high-fidelity POS system with three main pages: **Produk**, **Kasir**, and **Riwayat**. The focus is on Senior-level UI/UX using Material Design 3, 8dp grid system, and a soft/pastel monochromatic theme.

## User Review Required

> [!IMPORTANT]
> The theme will be shifted to a **Soft Indigo/Slate** monochromatic palette for a premium, clean look.
> Layouts will be designed to be **Adaptive**, looking great on both standard phones and larger devices.

## Proposed Changes

### Theme & Aesthetics

#### [MODIFY] [Color.kt](file:///C:/Users/USER/AndroidStudioProjects/kasirandroid/app/src/main/java/com/example/kasirandroid/ui/theme/Color.kt)
- Define a professional soft Indigo palette.
- Primary: `#3949AB`, OnPrimary: `#FFFFFF`, Secondary: `#5C6BC0`, SurfaceVariant: `#F0F2F5`.

#### [MODIFY] [Theme.kt](file:///C:/Users/USER/AndroidStudioProjects/kasirandroid/app/src/main/java/com/example/kasirandroid/ui/theme/Theme.kt)
- Configure `LightColorScheme` and `DarkColorScheme` with the new Indigo palette.
- Set global corner shapes (Medium = 12dp, Large = 16dp).

### Phase 1: Navigation & "Produk" Page

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USER/AndroidStudioProjects/kasirandroid/app/src/main/java/com/example/kasirandroid/MainActivity.kt)
- Refine `NavigationBar` with better labels and modern icons.
- Ensure `NavHost` transitions are smooth.

#### [NEW] `ui/screens/ProdukScreen.kt` (Redesign)
- **Top Bar**: Search bar integration + "Produk" title.
- **Filter Section**: Category chips (e.g., "Semua", "Makanan", "Minuman").
- **Product Card**: Minimalist cards with:
    - Bold price display.
    - Low stock indicator (Soft red badge if stock < 5).
    - Subtle elevation (1dp-2dp).
- **FAB**: Elegant "Add" button with shadow.

### Phase 3: "Kasir" (POS Screen) Implementation

Redesigning the **Kasir** page to be a high-performance, intuitive interface for processing sales.

#### [NEW] `ui/screens/KasirScreen.kt` (Redesign)
- **Split Layout**:
    - **Left/Main Area**: Product selection grid with search and category filtering (consistent with the Produk page).
    - **Right/Bottom Area**: A sleek Cart Summary. On mobile, this will be an expandable Bottom Sheet or a dedicated view.
- **Interactive Cart**:
    - Itemized list with "Quick Adjust" quantity buttons (+/-).
    - Swipe-to-remove gesture support.
- **Checkout Experience**:
    - Sticky bottom bar showing the Total Price.
    - **Payment Flow**: A clean dialog to confirm payment method and show change (kembalian).
    - **Success State**: A minimalist success dialog with a "Print Receipt" option (UI placeholder).

#### [MODIFY] [PosViewModel.kt](file:///C:/Users/USER/AndroidStudioProjects/kasirandroid/app/src/main/java/com/example/kasirandroid/ui/viewmodel/PosViewModel.kt)
- Add state for "Success/Error" feedback during checkout.
- Add logic for calculating change (kembalian).

### Phase 4: "Riwayat" (Transaction History) Implementation

Redesigning the **Riwayat** page to be a polished record-keeping interface with detailed transaction views.

#### [NEW] `ui/screens/RiwayatScreen.kt` (Redesign)
- **Top Section**:
    - Bold "Riwayat Transaksi" headline.
    - **Quick Filters**: Chips for "Hari Ini", "7 Hari Terakhir", "Semua".
- **Transaction Cards**:
    - `ElevatedCard` with 12dp rounded corners and subtle shadow.
    - **Header**: Date and Time formatted cleanly.
    - **Content**: Summary of items (e.g., "3 Item") and a large, bold Total Amount.
    - **Status Badge**: A "Lunas" badge in soft green for all completed transactions.
- **Receipt Dialog**:
    - Clicking a card opens a "Receipt" dialog showing the full itemized list, subtotal, and payment details.
- **Empty State**: A clean "Belum ada transaksi" message with a descriptive icon.

#### [MODIFY] [HistoryViewModel.kt](file:///C:/Users/USER/AndroidStudioProjects/kasirandroid/app/src/main/java/com/example/kasirandroid/ui/viewmodel/HistoryViewModel.kt)
- Add logic for filtering the `allTransactions` list based on the selected time range chip.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USER/AndroidStudioProjects/kasirandroid/app/src/main/java/com/example/kasirandroid/MainActivity.kt)
- Ensure the navigation routes correctly to the new `RiwayatScreen`.

## Verification Plan

### Manual Verification
- **Visual Check**: Open `ProdukScreen` preview and verify the 8dp spacing and color contrast.
- **Dark Mode**: Toggle system theme to ensure Indigo shades are pleasant in dark mode.
- **Responsiveness**: Rotate device to check grid adjustment.
