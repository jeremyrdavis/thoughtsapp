interface StatusBadgeProps {
  status: "APPROVED" | "REJECTED" | "IN_REVIEW";
}

export default function StatusBadge({ status }: StatusBadgeProps) {
  const cls =
    status === "APPROVED"
      ? "status-badge-approved"
      : status === "REJECTED"
        ? "status-badge-rejected"
        : "status-badge-in-review";

  const label = status === "IN_REVIEW" ? "In Review" : status.charAt(0) + status.slice(1).toLowerCase();

  return <span className={cls}>{label}</span>;
}
