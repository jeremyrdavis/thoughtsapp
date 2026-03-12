import { Link } from "react-router-dom";
import { Info, ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function EvaluationStats() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-display">Evaluation Statistics</h1>
        <Button variant="outline" asChild>
          <Link to="/evaluations"><ArrowLeft size={16} /> Back to Evaluations</Link>
        </Button>
      </div>
      <div className="flex items-center gap-3 rounded-lg border border-primary/20 bg-primary/5 p-4 text-sm">
        <Info size={20} className="text-primary" />
        No statistics available. Statistics will appear once thoughts have been evaluated.
      </div>
    </div>
  );
}
