import { Link } from "react-router-dom";
import { Info, BarChart3 } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function Evaluations() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-display">AI Evaluations</h1>
        <Button variant="outline" asChild>
          <Link to="/evaluations/stats"><BarChart3 size={16} /> View Statistics</Link>
        </Button>
      </div>
      <div className="flex items-center gap-3 rounded-lg border border-primary/20 bg-primary/5 p-4 text-sm">
        <Info size={20} className="text-primary" />
        Evaluations will be available once the AI evaluation service is connected.
      </div>
    </div>
  );
}
